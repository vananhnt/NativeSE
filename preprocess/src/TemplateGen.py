from jinja2 import Template
from pathlib import Path
from tqdm import tqdm
import xml.etree.ElementTree as ET
import subprocess as sp
import pandas as pd
import pickle
import random
import string
import time
import logging
import shutil
import sys

#filename = "logfile.log", filemode = "w"
logger = logging.getLogger()
handler = logging.StreamHandler(stream=sys.stdout) 
logger.addHandler(handler)
logger.setLevel(logging.INFO)

class TemplateGen:
    ACTIVITY_TEMPLATE = './data/ActivityWrapper.template'
    HYBRIDSE_DIR = '/home/va/git/HybridSE/src'
    ENTRY = {'onCreate':['bd'], 'buttonClicked':['view'], 'onStart':['intent'], \
             'onRequestPermissionsResult':['1', 'strArray', 'intArray']}
    RESULT_DIR ='./temp/result'
    FAMILY_CSV="./data/sha256_family.csv" 

    def __init__(self) -> None:
        self.root_package = None
        self.activities = {} #Dict of activity and package name
        self.entrypoints = {} #
        self.native_path = None
        self.classes = {}
        self.services = {}
        self.asyncTasks = {} 
        self.stat = (0, 0, 0)

    def parseXML(self, sample_dir):
        xmlfile = sample_dir + '/AndroidManifest.xml'
        try:
            tree = ET.parse(xmlfile)
    
            # get root element
            root = tree.getroot()

            # create empty list for news items
            activities_dict = {}
            services_dict = {}
            root_package = root.attrib['package']
            
            for item in root.findall('.//activity'):
                android_name = item.attrib['{http://schemas.android.com/apk/res/android}name']
                if android_name.startswith('.'):
                    class_name=android_name.replace('.','')
                    activities_dict[class_name] = root_package
                elif android_name.startswith(root_package) or android_name.startswith('com'):
                    paths = [x for x in android_name.split('.') if x]
                    class_name = paths[len(paths) - 1]
                    activities_dict[class_name] = '.'.join(paths[:-1])
                else:
                    paths = [x for x in android_name.split('.') if x]
                    class_name = paths[len(paths) - 1]
                    package_name = root_package + '.' + '.'.join(paths[:-1])
                    activities_dict[class_name] = root_package + '.' + '.'.join(paths[:-1])
            
            for item in root.findall('.//service'):
                android_name = item.attrib['{http://schemas.android.com/apk/res/android}name']
                if android_name.startswith('.'):
                    class_name=android_name.replace('.','')
                    services_dict[class_name] = root_package
                elif android_name.startswith(root_package) or android_name.startswith('com'):
                    paths = [x for x in android_name.split('.') if x]
                    class_name = paths[len(paths) - 1]
                    services_dict[class_name] = '.'.join(paths[:-1])
                else:
                    paths = [x for x in android_name.split('.') if x]
                    class_name = paths[len(paths) - 1]
                    package_name = root_package + '.' + '.'.join(paths[:-1])
                    services_dict[class_name] = package_name
            
            with open(sample_dir + '/method_dict.pkl', 'rb') as handle:
                pkl_dict = pickle.load(handle)
                async_dict = {}
                for task in pkl_dict['asyncTask']:
                    class_name = task.split('.')[-1]
                    package_name = task.replace('.'+class_name, '')
                    async_dict[class_name] = package_name
                self.asyncTasks = async_dict
                
                pkl_dict.pop('asyncTask')
                self.classes = pkl_dict
            
            self.native_path = self.__find_native(sample_dir)
            self.root_package = root_package
            self.activities = activities_dict
            self.services = self.__clear_path(services_dict)
        except Exception:
            pass

    def __clear_path(self, classdict):
        for k in classdict:
            classdict[k] = (classdict[k][:-1] if classdict[k][-1] =='.' else classdict[k]).replace("..",".")
        return classdict
    
    def __find_native(self, folder) -> str:
        for native_file in Path(folder).rglob("*.so"):
            return '/lib/' + native_file.parent.name + '/' + native_file.name

    def __random(type_list) -> list:
        res = []
        for t in type_list:
            if 'String' in t:
                res.append("\""+''.join(random.choices(string.ascii_lowercase, k=10))+"\"")
            elif 'FileDescriptor' in t:
                res.append("new FileDescriptor()")
            elif 'Integer' in t:
                res.append(str(random.randint(0, 100)))
            elif 'int' in t and '[]' not in t:
                res.append(str(random.randint(0, 100)))
            elif 'int' in t and '[]' in t:
                res.append('intArray')
            else:
                res.append('null')        
        return res
    
    def __random_sym(type_list) -> list:
        res = []
        for t in type_list:
            if 'String' in t:
                res.append("sym")
            elif 'Integer' in t:
                res.append("#sym")
            elif 'int' in t:
                res.append("sym")
            else:
                res.append("sym") 
        return res

    def __genAsync(self, async_name, async_path, entry_point, args_count=None, args_types=None):
        args_str = (',').join(TemplateGen.__random(args_types)) if args_types is not None else ''
        logger.debug(msg=async_name+'.'+entry_point+'('+args_str+')')
        with open('./data/AsyncTaskWrapper.template') as file_:
            struct_template = Template(file_.read(), trim_blocks=True)
            return struct_template.render(package_name=async_path,class_name=async_name,\
                                          entry_point=entry_point, args=args_str)

    def __genService(self, activity_name, activity_path, entry_point, args_count=None, args_types=None):
        args_str = (',').join(TemplateGen.__random(args_types)) if args_types is not None else ''
        logger.debug(msg=activity_name+'.'+entry_point+'('+args_str+')')
        with open('./data/ServiceWrapper.template') as file_:
            struct_template = Template(file_.read(), trim_blocks=True)
            return struct_template.render(package_name=activity_path,class_name=activity_name,\
                                          entry_point=entry_point, args=args_str)

    def __genActivity(self, activity_name, activity_path, entry_point, args_count=None, args_types=None):
        args_str = (',').join(TemplateGen.__random(args_types)) if args_types is not None else ''
        logger.debug(msg=activity_name+'.'+entry_point+'('+args_str+')')
        with open('./data/ActivityWrapper.template') as file_:
            struct_template = Template(file_.read(), trim_blocks=True)
            return struct_template.render(package_name=activity_path,class_name=activity_name,\
                                          entry_point=entry_point, args=args_str)
    
    def __genJpfFile(self, activity_name, activity_path, entry_point, args_count=None, args_types=None):
        args_count = args_count if args_count is not None else \
            (len(args_types) if args_types is not None else 0)
        args_str = (',').join(TemplateGen.__random_sym(args_types)) if args_types is not None else \
            (',').join(['sym']*args_count)

        with open('./data/JPF.template') as file_:
            template = Template(file_.read(), trim_blocks=True)
            return template.render(package_name=activity_path,class_name=activity_name,\
                                    native_lib=self.native_path, entry_point=entry_point, sym_str=args_str)
    
    def __stat(self, output:str) -> tuple:
        return (output.count('\n-'), output.count('\n->'), output.count("=== Call to library function"))

    def __sum_stat(self, output:str) -> tuple:
        a = self.__stat(output)
        self.stat = tuple(map(sum, zip(self.stat, a)))    

    def build():
        build_cmd = "bash {}/build.sh {}".format(TemplateGen.HYBRIDSE_DIR, TemplateGen.HYBRIDSE_DIR)
        build_run = sp.run(build_cmd, shell=True, capture_output=True, text=True)
        logger.debug(msg=build_run.stdout)

    def run_actitivy(self, sample_dir, class_name, package_name, entry, args_count=None, args_types=None):
        try:
            java_file=sample_dir + '/src/' + package_name.replace('.', '/') + '/' + class_name + 'Wrapper.java'
            f_wrapper = open(java_file, "w") 
            wrapper_content = self.__genActivity(class_name, package_name, entry, args_count=args_count, args_types=args_types)
           
            f_wrapper.write(wrapper_content)
            f_wrapper.close()
            
            f_jpf = open(sample_dir + '/'+class_name+'_'+entry+'.jpf', "w")
            jpf_content = self.__genJpfFile(class_name, package_name, entry, args_count=args_count, args_types=args_types)
            
            f_jpf.write(jpf_content)
            f_jpf.close()

            compile_cmd = "cd {} && javac -g {}".format(sample_dir+'/src', java_file.replace(sample_dir +'/src/', ''))
            sp.run(compile_cmd, shell=True, capture_output=True, text=True)
            print(compile_cmd)

            run_cmd = "bash {}/run.sh {} {}".format(self.HYBRIDSE_DIR, self.HYBRIDSE_DIR, Path(f_jpf.name).absolute())
            print(run_cmd)
            se_run = sp.run(run_cmd, shell=True, capture_output=True, text=True)
            logger.debug(msg=se_run.stdout)
            se_out = se_run.stdout
            print(se_run.stderr)
            f_out = open(sample_dir+'/output_'+class_name+'_'+entry+'.out', "w")
            f_out.write(se_out)
            f_out.close()
            
            self.__sum_stat(se_out)
            #logger.error(msg=se_run.stderr)
            # delete temperary file
            
            #Path(f_wrapper.name).unlink()
            #Path(f_jpf.name).unlink()
            Path(sample_dir+'/output.txt').unlink()  
            return se_out 
        except FileNotFoundError:
            pass
    
    def run_corana_direct(self, sample_dir, class_name, package_name, entry, args_count=None, args_types=None):
        try:
            native_lib = str(Path(sample_dir).absolute()) + '/' + self.native_path
            run_cmd = "bash {}/run_direct.sh {} {} {}".format(self.HYBRIDSE_DIR, self.HYBRIDSE_DIR, native_lib, entry)
            se_run = sp.run(run_cmd, shell=True, capture_output=True, text=True)
            se_out = se_run.stdout
            print(run_cmd)
            f_out = open(sample_dir+'/output_'+class_name+'_'+entry+'.out', "w")
            f_out.write(se_out)
            f_out.close()
            
            self.__sum_stat(se_out)
            print(se_run.stderr)
            #logger.error(msg=se_run.stderr)
            # delete temperary file
            return se_out   
        except FileNotFoundError:
            pass

    def run_service(self, sample_dir, class_name, package_name, entry, args_count=None, args_types=None):
        try:
            java_file=sample_dir + '/src/' + package_name.replace('.', '/') + '/' + class_name + 'Wrapper.java'
            f_wrapper = open(java_file, "w")    
            wrapper_content = self.__genService(class_name, package_name, entry, args_count=args_count, args_types=args_types)
            f_wrapper.write(wrapper_content)
            f_wrapper.close()
            
            f_jpf = open(sample_dir + '/'+class_name+'_'+entry+'.jpf', "w")
            jpf_content = self.__genJpfFile(class_name, package_name, entry, args_count=args_count, args_types=args_types)
            f_jpf.write(jpf_content)
            f_jpf.close()

            compile_cmd = "cd {} && javac -g {}".format(sample_dir+'/src', java_file.replace(sample_dir +'/src/', ''))
            sp.run(compile_cmd, shell=True,  capture_output=True, text=True)

            run_cmd = "bash {}/run.sh {} {}".format(self.HYBRIDSE_DIR, self.HYBRIDSE_DIR, Path(f_jpf.name).absolute())
            
            se_run = sp.run(run_cmd, shell=True, capture_output=True, text=True)
            logger.debug(msg=se_run.stdout)
            se_out = se_run.stdout
            
            f_out = open(sample_dir+'/output_'+class_name+'_'+entry+'.out', "w")
            f_out.write(se_out)
            f_out.close()
            
            self.__sum_stat(se_out)
            #logger.error(msg=se_run.stderr)
            # delete temperary file
            #Path(f_wrapper.name).unlink()
            #Path(f_jpf.name).unlink()
            #Path(sample_dir+'/output.txt').unlink()
            return se_out  
        except FileNotFoundError:
            pass
    
    def run_async(self, sample_dir, class_name, package_name, entry, args_count=None, args_types=None):
        try:
            java_file=sample_dir + '/src/' + package_name.replace('.', '/') + '/' + class_name + 'Wrapper.java'
            print(java_file)
            f_wrapper = open(java_file, "w")
            wrapper_content = self.__genAsync(class_name, package_name, entry, args_count=args_count, args_types=args_types)
            #print(wrapper_content)
            f_wrapper.write(wrapper_content)
            f_wrapper.close()
            
            f_jpf = open(sample_dir + '/'+class_name+'_'+entry+'.jpf', "w")
            jpf_content = self.__genJpfFile(class_name, package_name, entry, args_count=args_count, args_types=args_types)
            print(jpf_content)
            f_jpf.write(jpf_content)
            f_jpf.close()

            compile_cmd = "cd {} && javac -g {}".format(sample_dir+'/src', java_file.replace(sample_dir +'/src/', ''))
            sp.run(compile_cmd, shell=True,  capture_output=True, text=True)
            
            run_cmd = "bash {}/run.sh {} {}".format(self.HYBRIDSE_DIR, self.HYBRIDSE_DIR, Path(f_jpf.name).absolute())
            print(run_cmd)
            se_run = sp.run(run_cmd, shell=True, capture_output=True, text=True)
            logger.debug(msg=se_run.stdout)
            se_out = se_run.stdout
            print(se_out)
            f_out = open(sample_dir+'/output_'+class_name+'_'+entry+'.out', "w")
            f_out.write(se_out)
            f_out.close()
            
            self.__sum_stat(se_out)
            #logger.error(msg=se_run.stderr)
            # delete temperary file
            #Path(f_wrapper.name).unlink()
            #Path(f_jpf.name).unlink()
            #Path(sample_dir+'/output.txt').unlink()
            return se_out  
        except FileNotFoundError:
            pass
            
    def runEntryPoints(self, sample_dir:str):
        outputs=[]
        self.parseXML(sample_dir)
        
        apk_name = Path(sample_dir).stem
        class_name = ''

        for act in self.activities:
            for entry in self.__get_entrypoints(activity_name=act):
                outputs.append(self.run_actitivy(sample_dir, act, self.activities[act], entry, args_count=1, args_types=None))
            #outputs.append(self.run_actitivy(sample_dir, act, self.activities[act], 'buttonClicked', args_count=1, args_types=None))

        for ser in self.services:
            outputs.append(self.run_service(sample_dir, ser, self.services[ser], 'onStart', args_count=0, args_types=None))
            outputs.append(self.run_service(sample_dir, ser, self.services[ser], 'onCreate', args_count=0, args_types=None))
        
        for task in self.asyncTasks:
            outputs.append(self.run_async(sample_dir, task, self.asyncTasks[task], 'doInBackground', args_count=0, args_types=None))
        
        #for each native methods
        #   for mth_class in self.classes:
        #     for mth_sig in self.classes[mth_class]:
        #         print(mth_sig)
        #         param_list = mth_sig[mth_sig.find('(')+1:mth_sig.rfind(')')].strip().split(',')
        #         param_list = param_list.remove('') if '' in param_list else param_list #param list

        #         method_name = mth_sig.split('(')[0].split()[-1].replace(';','')[:mth_sig.find('(')+1].replace('(','').replace(')','') #entry point
        #         return_type = mth_sig.split()[-2]
        #         class_name = mth_class.split('.')[-1]   #activity class
        #         package_name = mth_class[:mth_class.rfind('.')]
        #         outputs.append(self.run_corana_direct(sample_dir, class_name, package_name, method_name, args_types=param_list, args_count=None))
                
        logger.debug(msg=self.stat)
    
        f_out = open(sample_dir+'/output_'+apk_name+'.out', "w")
        for ot in outputs:
            if ot is not None: f_out.write(ot)
        f_out.close()        

    def __get_entrypoints(self, activity_name):
        activity_full = self.activities[activity_name] + '.' + activity_name
        methods = self.classes[activity_full]
        entry_points = []
        for mth_class in self.classes:
            for mth_sig in self.classes[mth_class]:
                print(mth_sig)
                param_list = mth_sig[mth_sig.find('(')+1:mth_sig.rfind(')')].strip().split(',')
                param_list = param_list.remove('') if '' in param_list else param_list #param list

                method_name = mth_sig.split('(')[0].split()[-1].replace(';','')[:mth_sig.find('(')+1].replace('(','').replace(')','') #entry point
                return_type = mth_sig.split()[-2]
                class_name = mth_class.split('.')[-1]   #activity class
                package_name = mth_class[:mth_class.rfind('.')]
                
                if method_name in self.ENTRY.keys():
                    entry_points.append(method_name)
        return entry_points        

    def reset_dot(self):
        for file in Path(self.HYBRIDSE_DIR).glob("*.dot"):
            file.unlink()

    def get_dot(self, apk_name):
        for file in Path(self.HYBRIDSE_DIR).glob("*.dot"):
            Path(self.RESULT_DIR + '/' + apk_name).mkdir(parents=True, exist_ok=True)
            shutil.copy(file, self.RESULT_DIR + '/' + apk_name)

    def remove_file(self):  
        path = Path('./temp/java_projects/').glob('*')
        
        for file in (pbar:=tqdm(list(path))):
            Path(str(file) + '/src/android.jar').unlink(missing_ok=True)

if __name__ == "__main__":            
    gen = TemplateGen()
    TemplateGen.build()

    df = pd.read_csv(TemplateGen.FAMILY_CSV, delimiter=',',usecols=['sha256','family'])
    df = df.reindex(columns = ['sha256','family', 'bytecode','arm','API','time'])
    sample_dir = './temp/java_projects_01'
    save_file = './data/result_180523.csv'
    path = Path(sample_dir).glob("*")

    df_save = pd.read_csv(save_file, delimiter=',',usecols=['sha256', 'java', 'native', 'lib', 'time'])

    for file in (pbar:=tqdm(list(path))):
        pbar.set_postfix_str(file.name)
        apk_name = file.name
        gen = TemplateGen()
        if apk_name in df_save.sha256:
            print("Already processed")
            continue

        gen.reset_dot()
        start = time.time()
        gen.runEntryPoints(str(file.absolute()))
        exe_time = time.time() - start
        df.loc[df.sha256 == apk_name, ['bytecode','arm','API']] = list(gen.stat)
        df.loc[df.sha256 == apk_name, 'time'] = exe_time
        logger.info(msg=gen.stat)
        logger.info(msg=df.loc[df.sha256 == apk_name, 'time'])
        gen.get_dot(apk_name)
        with open('./data/result.csv', 'a') as file:
            line = apk_name+ ', ' + ','.join([str(x) for x in gen.stat]) + ', ' + str(exe_time) + '\n'
            file.write(line)
    df.to_csv('./run_stat.csv')
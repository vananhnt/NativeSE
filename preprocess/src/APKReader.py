from multiprocessing import Pool, freeze_support, RLock, current_process
from tqdm import tqdm
from pathlib import Path
import subprocess as sp
import shutil
import sys
import traceback
import xml.etree.ElementTree as ET
import zipfile
import pickle
import time
import logging

logger = logging.getLogger()
handler = logging.StreamHandler(stream=sys.stdout) 
logger.addHandler(handler)
logger.setLevel(logging.DEBUG)

class APKReader:
    DEX2JAR_DIR = "~/git/dex2jar/dex-tools/build/distributions/dex2jar-0.0.9.15"
    #DEX2JAR_DIR = "~/git/dex2jar/dex-tools/build/distributions/dex-tools-2.2-SNAPSHOT"
    TEMP="./temp_" + time.strftime("%Y%m%d_%H%M%S")
    APKTOOL_FDR= TEMP+ "/apktool_folder"
    DEX2JAR_INP= TEMP+ "/dex2jar_input"
    DEX2JAR_OUT= TEMP+ "/dex2jar_output"
    JAVA_PROJECT=TEMP+ "/java_projects" 
    BASE_PROJECT="./data/base_project"
    CSV_FILE="transfer_0507.csv"
    APK_NAME = ""
    
    def __init__(self) -> None:
        Path(self.TEMP).mkdir(parents=True, exist_ok=True)
        Path(self.APKTOOL_FDR).mkdir(parents=True, exist_ok=True)
        Path(self.DEX2JAR_INP).mkdir(parents=True, exist_ok=True)
        Path(self.DEX2JAR_OUT).mkdir(parents=True, exist_ok=True)

    ## 
    # Run APK function
    ##
    def run_apktool(self, apk_fpath):
        file_path = Path(apk_fpath)
        apk_name = file_path.stem
        self.APK_NAME = apk_name
        family_name = str(file_path.parent).split('/')[-2]

        if Path(self.JAVA_PROJECT + '/' +apk_name).exists():
            print("Already processed!")
            return
        try:
            decode_cmd = "apktool decode {} -f -o {}/{}.out".format(file_path.resolve(), self.APKTOOL_FDR, apk_name)
            sp.run(decode_cmd, shell=True)

            rebuild_cmd = "apktool build --debug -f {}/{}.out -o {}/{}".format(self.APKTOOL_FDR, apk_name, self.DEX2JAR_INP, apk_name)
            sp.run(rebuild_cmd,   shell=True)
            
            d2j_cmd = "{}/d2j-dex2jar.sh -f -d {}/{} -o {}/{}.jar" \
                        .format(self.DEX2JAR_DIR, self.DEX2JAR_INP, apk_name, self.DEX2JAR_OUT, apk_name)
            sp.run(d2j_cmd,  shell=True)
            
            #Count native functions
            findnative_cmd = "jar tf {}/{}.jar | grep '.class$' | tr / . | sed 's/\.class$//'| xargs javap -protected -cp {}/{}.jar | grep 'native'" \
                        .format(self.DEX2JAR_OUT, apk_name, self.DEX2JAR_OUT, apk_name)
            findnative_p = sp.run(findnative_cmd, shell=True, capture_output=True, text=True)
            native_count = findnative_p.stdout.strip().count('native')
            print("Number of native functions:", native_count)
            with open(self.CSV_FILE, 'a') as file:
                in_lib = 'yes' if Path(self.APKTOOL_FDR+'/'+apk_name+'.out/lib/').exists() else 'no'
                line = apk_name+ ', ' + str(native_count) + ', ' + in_lib + ', ' + family_name + '\n'
                file.write(line)

            #Copy base project when /lib folder exists
            if native_count > 0 and Path(self.APKTOOL_FDR+'/'+apk_name+'.out/lib/').exists():
                target_project = self.JAVA_PROJECT+'/' + apk_name
                shutil.copytree(self.BASE_PROJECT, target_project, dirs_exist_ok=True)
                with zipfile.ZipFile(self.DEX2JAR_OUT + '/' + apk_name + '.jar', 'r') as zip_ref:
                    zip_ref.extractall(target_project + '/src')
                shutil.copy(self.APKTOOL_FDR + '/' + apk_name + '.out/AndroidManifest.xml', target_project)
                shutil.copytree(self.APKTOOL_FDR +'/'+ apk_name + '.out/lib/', target_project + "/lib", dirs_exist_ok=True)

                #Write all native method names
                #And check if class is asynctask
                classes={}
                classes['asyncTask'] = []
                findclass_cmd = "jar tf {}/{}.jar | grep '.class$' | tr / . | sed 's/\.class$//'".format(self.DEX2JAR_OUT, apk_name)
                findclass_p = sp.run(findclass_cmd, shell=True, capture_output=True, text=True)
                
                all_classes = findclass_p.stdout.strip().splitlines()
                for cls in all_classes:
                    class_name=None
                    findmethod_cmd = "javap -cp {}/{}.jar {}".format(self.DEX2JAR_OUT, apk_name, cls)
                    findmethod_p = sp.run(findmethod_cmd, shell=True, capture_output=True, text=True)
                    mth_content = findmethod_p.stdout.strip()
                    
                    if ' native ' in mth_content:
                        class_name = cls.split('$')[0].strip()
                        classes[class_name] = []
                        for native_mth in mth_content.splitlines():
                            if ' native ' in native_mth:
                                classes[class_name].append(native_mth.strip())
                    
                    if 'activity' in mth_content.casefold():
                        class_name = cls.split('$')[0].strip()
                        classes[class_name] = []
                        for native_mth in mth_content.splitlines():
                            if 'public' in native_mth: 
                                # filter public methods
                                if ' class ' not in native_mth and len(native_mth.split()) > 2:
                                    classes[class_name].append(native_mth.strip())

                    if 'android.os.AsyncTask' in mth_content:
                        class_name = cls.split('$')[0].strip()
                        classes['asyncTask'].append(class_name)
                    
                with open(target_project + '/method_dict.pkl', 'wb') as handle:
                    pickle.dump(classes, handle)

            #shutil.rmtree(self.APKTOOL_FDR + '/' + apk_name + '.out',  ignore_errors=True)
            #Path(self.DEX2JAR_INP + '/' + apk_name).unlink(missing_ok=True)
            #Path(self.DEX2JAR_OUT + '/' + apk_name + '.jar').unlink(missing_ok=True)

        except Exception :
            print(traceback.format_exc())

    ## 
    # Util functions
    ##
    def __check_lib(self, apk_fpath):
        file_path = Path(apk_fpath)
        apk_name = file_path.stem
        count = 0
        print(file_path)

        if Path(self.JAVA_PROJECT + '/' +apk_name).exists(): 
            print("Already processed!")
            count = 1
        try:
            decode_cmd = "apktool decode {} -f -o {}/{}.out".format(file_path.resolve(), self.APKTOOL_FDR, apk_name)
            sp.run(decode_cmd, shell=True)
            rebuild_cmd = "apktool build --debug -f {}/{}.out -o {}/{}".format(self.APKTOOL_FDR, apk_name, self.DEX2JAR_INP, apk_name)
            sp.run(rebuild_cmd, shell=True)
            
            if Path(self.APKTOOL_FDR+'/'+apk_name+'.out/lib/armeabi').exists():
                count = 1
        except Exception :
            print(Exception)

        if count > 0:
            with open(self.CSV_FILE, 'a') as file:
                line = apk_name+ '\n'
                file.write(line)
        shutil.rmtree(self.APKTOOL_FDR + '/' + apk_name + '.out',  ignore_errors=True)
        return count

    def count_lib(self, dir_path):
        count = 0
        path = Path(dir_path).rglob("*/*")
        for file in (pbar:=tqdm(list(path))):
            count = count + self.__check_lib(file)
        print(count)

    def func(self, batch, tqdm_index):
        with tqdm(batch, position=tqdm_index) as progress:
            for file in progress:
                progress.set_postfix_str(file.stem)
                self.run_apktool(file)

    def split_chunk(full, n):
        (chunk_size, mod_size) = divmod(len(full), n) 
        chunk_sizes = [chunk_size for i in range(n-1)] + [chunk_size+mod_size]
        i = 0
        for k_size in chunk_sizes:
            yield full[i: i + k_size]
            i = i + k_size

    ##
    # Iterate functions
    ##
    def analyse_file(self, file_path):
        path = Path(file_path)
        self.run_apktool(path)
        return self.JAVA_PROJECT + '/' + self.APK_NAME
    
    def analyse_dir_multiproc(self, dir_path):
        n_processes = 5
        freeze_support()
        file_list = list(Path(dir_path).rglob("*/*.apk"))
        file_chunks = APKReader.split_chunk(file_list, n_processes)
        pool = Pool(n_processes, initializer=tqdm.set_lock, initargs=(tqdm.get_lock(),))
        
        results = pool.starmap(self.func, [(batch, idx) for idx, batch in enumerate(file_chunks)])
        print("DONE.")
        return self.JAVA_PROJECT 

    def analyse_dir(self, dir_path):
        file_list = list(Path(dir_path).rglob("*.apk"))
        for file in (pbar:=tqdm(file_list)):
            self.run_apktool(file)
        print("DONE.")
        return self.JAVA_PROJECT

if __name__ == "__main__": 
    args = sys.argv[1:]
    APKReader().analyse_dir(args[0])  #args[0] is path to an APK file
    #APKReader().analyse_file(args[0])
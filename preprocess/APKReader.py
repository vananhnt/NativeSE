import subprocess as sp
import shutil
import re
import csv
from tqdm import tqdm
import zipfile
import pickle
from pathlib import Path
import xml.etree.ElementTree as ET
import uuid

class APKReader:
    DEX2JAR_DIR = "~/git/dex2jar/dex-tools/build/distributions/dex2jar-0.0.9.15"
    TEMP="./temp_" + uuid.uuid4().hex
    APKTOOL_FDR= TEMP+ "/apktool_folder"
    DEX2JAR_INP= TEMP+ "/dex2jar_input"
    DEX2JAR_OUT= TEMP+ "/dex2jar_output"
    JAVA_PROJECT=TEMP+ "/java_projects" 
    BASE_PROJECT="./data/base_project"
    CSV_FILE = "./stat_1.csv"
    
    def __init__(self) -> None:
        Path(self.TEMP).mkdir(parents=True, exist_ok=True)
        Path(self.APKTOOL_FDR).mkdir(parents=True, exist_ok=True)
        Path(self.DEX2JAR_INP).mkdir(parents=True, exist_ok=True)
        Path(self.DEX2JAR_OUT).mkdir(parents=True, exist_ok=True)

    def run_apktool(self, apk_fpath):
        file_path = Path(apk_fpath)
        apk_name = file_path.stem
        print(file_path)
        if Path(self.JAVA_PROJECT + '/' +apk_name).exists(): 
            print("Already processed!")
            return
        #APKTool
        try:
            decode_cmd = "apktool decode {} -f -o {}/{}.out".format(file_path.resolve(), self.APKTOOL_FDR, apk_name)
            sp.run(decode_cmd, shell=True)
            rebuild_cmd = "apktool build --debug -f {}/{}.out -o {}/{}".format(self.APKTOOL_FDR, apk_name, self.DEX2JAR_INP, apk_name)
            sp.run(rebuild_cmd, shell=True)
            
            #DEX2JAR
            #d2j_cmd = "{}/d2j-dex2jar.sh -f -d {}/{} -o {}/{}.jar" \
            d2j_cmd = "{}/d2j-dex2jar.sh -f -d {}/{} -o {}/{}.jar" \
                        .format(self.DEX2JAR_DIR, self.DEX2JAR_INP, apk_name, self.DEX2JAR_OUT, apk_name)
            sp.run(d2j_cmd, shell=True)

            #Count native
            findnative_cmd = "jar tf {}/{}.jar | grep '.class$' | tr / . | sed 's/\.class$//'| xargs javap -protected -cp {}/{}.jar | grep 'native'" \
                        .format(self.DEX2JAR_OUT, apk_name, self.DEX2JAR_OUT, apk_name)
            findnative_p = sp.run(findnative_cmd, shell=True, capture_output=True, text=True)
            native_count = findnative_p.stdout.strip().count('native')
            
            #Copy base project
            if native_count > 0 and Path(self.APKTOOL_FDR+'/'+apk_name+'.out/lib/').exists():
                print(native_count)
                target_project = self.JAVA_PROJECT+'/'+apk_name
                shutil.copytree(self.BASE_PROJECT, target_project, dirs_exist_ok=True)
                with zipfile.ZipFile(self.DEX2JAR_OUT+'/'+apk_name+'.jar', 'r') as zip_ref:
                    zip_ref.extractall(target_project+'/src')
                shutil.copy(self.APKTOOL_FDR+'/'+apk_name+'.out/AndroidManifest.xml', target_project)
                shutil.copytree(self.APKTOOL_FDR+'/'+apk_name+'.out/lib/', target_project, dirs_exist_ok=True)

                #Write all native method names
                classes={}
                findclass_cmd = "jar tf {}/{}.jar | grep '.class$' | tr / . | sed 's/\.class$//'".format(self.DEX2JAR_OUT, apk_name)
                findclass_p = sp.run(findclass_cmd, shell=True, capture_output=True, text=True)
                
                all_classes = findclass_p.stdout.strip().splitlines()
                for cls in all_classes:
                    class_name=None
                    findmethod_cmd = "javap -cp {}/{}.jar {}".format(self.DEX2JAR_OUT, apk_name, cls)
                    findmethod_p = sp.run(findmethod_cmd, shell=True, capture_output=True, text=True)
                    mth_content = findmethod_p.stdout.strip()
                    if 'native' in mth_content:
                        class_name = cls.split('$')[0].strip()
                        classes[class_name] = []
                        for native_mth in mth_content.splitlines():
                            if 'native' in native_mth:
                                classes[class_name].append(native_mth.strip())

                with open(target_project + '/method_dict.pkl', 'wb') as handle:
                    pickle.dump(classes, handle)

        except Exception :
            print(Exception)

        shutil.rmtree(self.APKTOOL_FDR + '/' + apk_name + '.out',  ignore_errors=True)
        Path(self.DEX2JAR_INP + '/' + apk_name).unlink(missing_ok=True)
        Path(self.DEX2JAR_OUT + '/' + apk_name + '.jar').unlink(missing_ok=True)

    def __check_lib(self, apk_fpath):
        file_path = Path(apk_fpath)
        apk_name = file_path.stem
        count = 0
        print(file_path)
        if Path(self.JAVA_PROJECT + '/' +apk_name).exists(): 
            print("Already processed!")
            count = 1
        #APKTool
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
            with open('./data/count.csv', 'a') as file:
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

    def analyse_dir(self,dir_path):
        path = Path(dir_path).rglob("*/*.apk")
        for file in (pbar:=tqdm(list(path))):
            print(file)
            self.run_apktool(file)
        return self.JAVA_PROJECT 

    def analyse_file(self, file_path):
        path = Path(file_path)
        self.run_apktool(path)
        return self.JAVA_PROJECT
    
if __name__ == "__main__":    
#APKReader().analyse_dir("./samples/towelroot")
#APKReader().run_apktool("./samples/AN.apk")
    APKReader().count_lib("/media/data/drebin")
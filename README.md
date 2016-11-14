# Nuwa集成步骤

1. 在工程根目录下的 `build.gradle` 文件中添加如下代码:   

	```groovy
    classpath 'com.android.tools.build:gradle:1.2.3'
    classpath 'cn.jiajixin.nuwa:gradle:1.2.2'
    classpath 'com.github.dcendents:android-maven-gradle-plugin:1.3'
    classpath 'com.jfrog.bintray.gradle:gradle-bintray-plugin:1.2'
	```

  添加后你的 `build.gradle` 文件的代码类似于下面这样:   

	```groovy
	buildscript {
	    repositories {
	        jcenter()
	    }
	    dependencies {
	        classpath 'com.android.tools.build:gradle:1.2.3'
	        classpath 'cn.jiajixin.nuwa:gradle:1.2.2'
	        classpath 'com.github.dcendents:android-maven-gradle-plugin:1.3'
	        classpath 'com.jfrog.bintray.gradle:gradle-bintray-plugin:1.2'
	    }
	}
	```

2. 在你自己的module目录(例如: app module)下的 `build.gradle` 文件中添加如下代码:   

	```groovy
	apply plugin: "cn.jiajixin.nuwa"
	```

  还要添加如下依赖:   

	```groovy
	dependencies {
	    compile 'cn.jiajixin.nuwa:nuwa:1.0.0'
	}
	```

# Nuwa使用步骤
1. 取消 Android Studio 的 Instant Run 功能. 因为 Instant Run 与热修复技术会造成冲突. 具体设置方法: File ---> Settings... ---> Build, Execution, Deployment ---> Instant Run ---> 取消勾选第一个选项(Enable Instatn Run...).

2. 在你的 `Application` 类中添加如下代码:   

	```groovy
	@Override
	protected void attachBaseContext(Context base) {
	    super.attachBaseContext(base);
	    Nuwa.init(this);
	    Nuwa.loadPatch(this, patchFilePath); // patchFilePath 是patch jar包最终存放在手机中的路径.
	}
	```
3. 如果开启了混淆, 则需要在你的module目录下的 `proguard-rules.pro` 文件中添加如下代码:   

	```java
	-keep class cn.jiajixin.nuwa.** { *; }
	```

4. 运行app module, 此时屏幕上会显示如下文字:   

	```java
	This is a BUG !!!
	```

5. 将 `app/build/outputs/nuwa` 文件夹复制到其他地方, 例如: /local 目录下.   

6. 模拟修复bug的过程. 将 `com.example.nuwa_demo.TextFactory.java` 文件中的 `createText()` 方法的返回值修改为 `"bug fixed"`.   

7. 打补丁包.   
 
 在工程根目录下, 执行打补丁的指令. 指令格式为:

	```shell
	./gradlew clean 打包指令 -P NuwaDir=刚才保存的nuwa文件夹的目录(例如: /local/nuwa/)
	```

 打包指令有两种:   

	* nuwaPatches

		该指令将会针对每一个 variant 都分别打出一个patch.jar包.

	* nuwa${variant.name.capitalize()}Patch

		该指令只会打出我们在 `build.gradle` 文件中指定的 variant 所对应的那个patch.jar包.   


 所以, 完整的打包指令可以是以下任意一个   

	- ./gradlew clean nuwaPatches -P NuwaDir=/local/nuwa/
	- ./gradlew clean nuwaDebugPatch -P NuwaDir=/local/nuwa/
	- ./gradlew clean nuwaReleasePatch -P NuwaDir=/local/nuwa/
	- 其他

 你可以根据你自己设定的 variant 对打包指令做出相应的修改.   

 使用以上指令打出的补丁包名称都为 `patch.jar`, 补丁包的存放位置是: 我们自己module/build/outputs/nuwa文件夹.我们需要修改补丁包的名称, 使其与 java 代码中预先定义的补丁包名称相同. 例如: 我们这个demo就需要将补丁包更名为 `NuwaDemoPatch.jar`.   

   
8. 模拟安装补丁的过程. 我们将补丁包通过 `adb push` 指令发送到手机上特定位置, 该位置需要与java代码中预先定义好的 patchFilePath 保持一致. 例如: 我们这个demo就需要将补丁包发送到手机 /sdcard/目录下.

9. 重启APP, 补丁即可生效. 生效后, 界面上会显示如下文字, 表示补丁安装成功.    

	```java
	bug fixed
	```

# 常见问题及解决方案
1. **Error:Cannot get property 'taskDependencies' on null object**   
	根目录下 `build.gradle`文件中 android-gradle 插件的版本设置过高, 请使用原工程中的低版本.    
	
	```groovy
	classpath 'com.android.tools.build:gradle:1.2.3'
	```

2. **UnsupportedMethodException: Unsupported method: AndroidProject.getPluginGeneration()...**   
	未取消 Instant Run 功能. 因为 Instant Run 与热修复技术会造成冲突. 请取消 Instant Run.   

3. **$ANDROID_HOME is not defined**   
	未添加 ANDROID_HOME 环境变量, 请先添加, 其值就是Android sdk的sdk文件夹所在的全路径. 

4. **Execution failed for task ':app:nuwaClassBeforeDexRelease'**   
   **Caused by: java.lang.ArrayIndexOutOfBoundsException**  
   **at cn.jiajixin.nuwa.NuwaPlugin$_apply_closure1_closure2_closure9_closure12.doCall(NuwaPlugin.groovy:127)** 
   推测应该是该作者引用他自己的另外一个开源库`NuwaPlugin`中有bug造成的. 将 app/build.gradle 文件中的 release混淆开关打开即可.

   ```groovy
    buildTypes {
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
   ```

5. **Failed to apply plugin [id 'com.github.dcendents.android-maven']**   
   **Plugin with id 'com.github.dcendents.android-maven' not found.**
   在根目录下的 build.gradle 中添加如下依赖: 
   
   ```groovy
   classpath 'com.github.dcendents:android-maven-gradle-plugin:1.3'
   classpath 'com.jfrog.bintray.gradle:gradle-bintray-plugin:1.2'
   ```
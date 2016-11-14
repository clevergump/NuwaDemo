# Nuwa集成步骤:

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

# Nuwa使用步骤:

1. 在你的 `Application` 类中添加如下代码:   

	```groovy
	@Override
	protected void attachBaseContext(Context base) {
	    super.attachBaseContext(base);
	    Nuwa.init(this);
	    Nuwa.loadPatch(this, patchFilePath); // patchFilePath 是patch jar包最终存放在手机中的路径.
	}
	```
2. 如果开启了混淆, 则需要在你的module目录下的 `proguard-rules.pro` 文件中添加如下代码:   

	```java
	-keep class cn.jiajixin.nuwa.** { *; }
	```

3. 运行app module, 此时屏幕上会显示如下文字:   

	```java
	This is a BUG !!!
	```

4. 将 `app/build/output/nuwa` 文件夹复制到其他地方, 例如: /local 目录下.   

5. 模拟修复bug的过程. 将 `com.example.nuwa_demo.TextFactory.java` 文件中的 `createText()` 方法的返回值修改为 `"bug fixed"`.   

6. 打补丁包.       
指令格式为:

	```shell
	./gradlew clean 打包指令 -P NuwaDir=刚才保存的nuwa文件夹的目录(例如: /local/nuwa)
	```

 打包指令有两种:   

	* nuwaPatches

		该指令将会针对每一个 variant 都分别打出一个patch.jar包.

	* nuwa${variant.name.capitalize()}Patch

		该指令只会打出我们在 `build.gradle` 文件中指定的 variant 所对应的那个patch.jar包.   


 所以, 完整的打包指令可以是以下任意一个   

	- ./gradlew clean nuwaPatches -P NuwaDir=/local/nuwa
	- ./gradlew clean nuwaDebugPatch -P NuwaDir=/local/nuwa
	- ./gradlew clean nuwaReleasePatch -P NuwaDir=/local/nuwa
	- 其他

 你可以根据你自己设定的 variant 对打包指令做出相应的修改.   

 使用以上指令打出的补丁包名称都为 `patch.jar`, 我们需要修改该补丁包的名称, 使其与 java 代码中预先定义的补丁包名称相同. 例如: 我们这个demo就需要把补丁包更名为 `NuwaDemoPatch.jar`---.   

   
7. 模拟安装补丁的过程. 我们将补丁包通过adb指令发送到手机上特定位置, 该位置需要与java代码中预先定义好的 patchFilePath 保持一致. 例如: 我们这个demo就需要将补丁包发送到手机 /sdcard/目录下.

8. 重启APP, 补丁即可生效. 生效后, 界面上会显示如下文字, 表示补丁安装成功.    

	```java
	bug fixed
	```
/*
	forma de invocación de método call:
	def ejecucion = load 'gradle.groovy'
	ejecucion.call()
*/

def call(){
	figlet "gradle"
	figlet plType
	println(stages)
	stage('Build & Unit Test') {
		STAGE = env.STAGE_NAME
		figlet "Stage: ${env.STAGE_NAME}"
		sh "gradle build"
	}
	
	stage('Sonar') {
		STAGE = env.STAGE_NAME
		figlet "Stage: ${env.STAGE_NAME}"
		def scannerHome = tool 'sonar-scanner';
		withSonarQubeEnv('sonar-server') {
			sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle-staller14 -Dsonar.sources=src -Dsonar.java.binaries=build"
		}
	}
	
	stage('Run') {
		STAGE = env.STAGE_NAME
		figlet "Stage: ${env.STAGE_NAME}"
		sh "gradle bootRun &"
		sleep 20
	}

	stage('TestApp') {
		STAGE = env.STAGE_NAME
		figlet "Stage: ${env.STAGE_NAME}"
		sh "curl -X GET 'http://localhost:8082/rest/mscovid/test?msg=testing'"
	}

	stage('Nexus') {
		STAGE = env.STAGE_NAME
		figlet "Stage: ${env.STAGE_NAME}"
		nexusPublisher nexusInstanceId: 'nexus-server',
		nexusRepositoryId: 'test-nexus',
		packages: [
			[
				$class: 'MavenPackage',
				mavenAssetList: [
					[classifier: '', extension: '', filePath: "${env.WORKSPACE}/build/libs/DevOpsUsach2020-0.0.1.jar"]
				],
				mavenCoordinate: [
					artifactId: 'DevOpsUsach2020',
					groupId: 'com.devopsusach2020',
					packaging: 'jar',
					version: '0.0.1'
				]
			]
		]
	}
}

def stageBuild(){
	stage('Build & Unit Test') {
		STAGE = env.STAGE_NAME
		figlet "Stage: ${env.STAGE_NAME}"
		sh "gradle build"
	}
}

def stageSonar(){
	stage('Sonar') {
		STAGE = env.STAGE_NAME
		figlet "Stage: ${env.STAGE_NAME}"
		def scannerHome = tool 'sonar-scanner';
		withSonarQubeEnv('sonar-server') {
			sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle-staller14 -Dsonar.sources=src -Dsonar.java.binaries=build"
		}
	}
}

def stageRun(){
	stage('Run') {
		STAGE = env.STAGE_NAME
		figlet "Stage: ${env.STAGE_NAME}"
		sh "gradle bootRun &"
		sleep 20
	}
}

def stageTestApp(){
	stage('TestApp') {
		STAGE = env.STAGE_NAME
		figlet "Stage: ${env.STAGE_NAME}"
		sh "curl -X GET 'http://localhost:8082/rest/mscovid/test?msg=testing'"
	}
}

def stageNexus(){
	stage('Nexus') {
		STAGE = env.STAGE_NAME
		figlet "Stage: ${env.STAGE_NAME}"
		nexusPublisher nexusInstanceId: 'nexus-server',
		nexusRepositoryId: 'test-nexus',
		packages: [
			[
				$class: 'MavenPackage',
				mavenAssetList: [
					[classifier: '', extension: '', filePath: "${env.WORKSPACE}/build/libs/DevOpsUsach2020-0.0.1.jar"]
				],
				mavenCoordinate: [
					artifactId: 'DevOpsUsach2020',
					groupId: 'com.devopsusach2020',
					packaging: 'jar',
					version: '0.1.0'
				]
			]
		]
	}
}

def stageNexusCD(){
	stage('Nexus') {
		STAGE = env.STAGE_NAME
		figlet "Stage: ${env.STAGE_NAME}"
		nexusPublisher nexusInstanceId: 'nexus-server',
		nexusRepositoryId: 'test-nexus',
		packages: [
			[
				$class: 'MavenPackage',
				mavenAssetList: [
					[classifier: '', extension: '', filePath: "${env.WORKSPACE}/DevOpsUsach2020-0.1.0.jar"]
				],
				mavenCoordinate: [
					artifactId: 'DevOpsUsach2020',
					groupId: 'com.devopsusach2020',
					packaging: 'jar',
					version: '1.0.0'
				]
			]
		]
	}
}

def stageDownloadNexus(){
	stage('DownloadNexus') {
		STAGE = env.STAGE_NAME
		figlet "Stage: ${env.STAGE_NAME}"
		sh "curl -X GET 'http://localhost:8081/repository/test-nexus/com/devopsusach2020/DevOpsUsach2020/0.1.0/DevOpsUsach2020-0.1.0.jar' -o ${env.WORKSPACE}/DevOpsUsach2020-0.1.0.jar"
	}
}

def stageRunDownloadedJar(){
	stage('Run') {
		STAGE = env.STAGE_NAME
		figlet "Stage: ${env.STAGE_NAME}"
		sh "java -jar ${env.WORKSPACE}/DevOpsUsach2020-0.1.0.jar &"
		sleep 30
	}
}

def runGradleStages(stages){
	def map = [1:[name:'build', priority:1, dependencies:null],
			   2:[name:'sonar', priority:2, dependencies:'build'],
			   3:[name:'run', priority:3, dependencies:'build'],
			   4:[name:'testapp', priority:4, dependencies:'run'],
			   5:[name:'nexus', priority:5, dependencies:'build']]
	stgsToProc = [:]
	addStage(stages,map,stgsToProc)
	def aux = stgsToProc.sort()
	def keyS = aux.keySet()
	keyS.each {
		def stageName = stgsToProc.get(it).name
		switch(stageName) {
			case "build":
				println "g build: ${stageName}"
				stageBuild()
				break
			case "sonar":
				println "g sonar: ${stageName}"
				stageSonar()
				break
			case "run":
				println "g run: ${stageName}"
				stageRun()
				break
			case "testapp":
				println "g testapp: ${stageName}"
				stageTestApp()
				break
			case "nexus":
				println "g nexus: ${stageName}"
				stageNexus()
				break
			default:
				STAGE_ERR_MSG = "Stage no válida: ${stagesList[i]}"
				println STAGE_ERR_MSG
				break
		}
	}
}

def addStage(stagesStr,map,stgMap){
	def stagesList = stagesStr.split(',')
	for(int i = 0;i<stagesList.length;i++){
		def stage = map.find { it.value.name == stagesList[i]}
		if(stage!=null){
			if(!stgMap.containsKey(stage.key)){
				stgMap.put(stage.key,stage.value)
				if(stage.value.dependencies!=null)
					addStage(stage.value.dependencies,map,stgMap)
			}
			else{
				println "Stage fue previamente agregada: $stage.key; $stage.value.name"
			}
		}
		else{
			STAGE_ERR_MSG = "Stage no válida: ${stagesList[i]}"
			println STAGE_ERR_MSG
			return 0
		}
	}
}

def runCI(){
	figlet "CI"
	stageBuild()
	stageSonar()
	stageRun()
	stageTestApp()
	stageNexus()
}

def runCD(){
	figlet "CD"
	stageDownloadNexus()
	stageRunDownloadedJar()
	stageTestApp()
	stageNexusCD()
}

return this;

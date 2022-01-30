/*
	forma de invocación de método call:
	def ejecucion = load 'gradle.groovy'
	ejecucion.call()
*/

def call(){
  
	stage('Build & Unit Test') {
		STAGE = env.STAGE_NAME
		println "Stage: ${env.STAGE_NAME}"
		sh "gradle build"
	}
	
	stage('Sonar') {
		STAGE = env.STAGE_NAME
		println "Stage: ${env.STAGE_NAME}"
		def scannerHome = tool 'sonar-scanner';
		withSonarQubeEnv('sonar-server') {
			sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle-staller14 -Dsonar.sources=src -Dsonar.java.binaries=build"
		}
	}
	
	stage('Run') {
		STAGE = env.STAGE_NAME
		println "Stage: ${env.STAGE_NAME}"
		sh "gradle bootRun &"
		sleep 20
	}

	stage('TestApp') {
		STAGE = env.STAGE_NAME
		println "Stage: ${env.STAGE_NAME}"
		sh "curl -X GET 'http://localhost:8082/rest/mscovid/test?msg=testing'"
	}

	stage('Nexus') {
		STAGE = env.STAGE_NAME
		println "Stage: ${env.STAGE_NAME}"
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
		println "Stage: ${env.STAGE_NAME}"
		sh "gradle build"
	}
}

def stageSonar(){
	stage('Sonar') {
		STAGE = env.STAGE_NAME
		println "Stage: ${env.STAGE_NAME}"
		def scannerHome = tool 'sonar-scanner';
		withSonarQubeEnv('sonar-server') {
			sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle-staller14 -Dsonar.sources=src -Dsonar.java.binaries=build"
		}
	}
}

def stageRun(){
	stage('Run') {
		STAGE = env.STAGE_NAME
		println "Stage: ${env.STAGE_NAME}"
		sh "gradle bootRun &"
		sleep 20
	}
}

def stageTestApp(){
	stage('TestApp') {
		STAGE = env.STAGE_NAME
		println "Stage: ${env.STAGE_NAME}"
		sh "curl -X GET 'http://localhost:8082/rest/mscovid/test?msg=testing'"
	}
}

def stageNexus(){
	stage('Nexus') {
		STAGE = env.STAGE_NAME
		println "Stage: ${env.STAGE_NAME}"
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

def runGradleStages(stages){
	println(stages)
	def map = [1:[name:'build', priority:1, dependencies:null],
			   2:[name:'sonar', priority:2, dependencies:'build'],
			   3:[name:'run', priority:3, dependencies:'build'],
			   4:[name:'testapp', priority:4, dependencies:'run'],
			   5:[name:'nexus', priority:5, dependencies:'build']]
	stgsToProc = [:]
	addStage(stages,map,stgsToProc)
	def keyS = stgsToProc.sort { a, b -> a.value.priority <=> b.value.priority }.keySet()
	println(stgsToProc)
	keyS.each {
		def stageName = stgsToProc.get(it).name
		switch(stageName) {
			case "build":
				println "g build: ${stageName}"
				break
			case "sonar":
				println "g sonar: ${stageName}"
				break
			case "run":
				println "g run: ${stageName}"
				break
			case "testapp":
				println "g testapp: ${stageName}"
				break
			case "nexus":
				println "g nexus: ${stageName}"
				break
			default:
				STAGE_ERR_MSG = "Stage no válida: ${stagesList[i]}"
				println STAGE_ERR_MSG
				break
		}
	}
}

def addStage(stagesStr,map,stgsToProc){
	def stagesList = stagesStr.split(',')
	for(int i = 0;i<stagesList.length;i++){
		def stage = map.find { it.value.name == stagesList[i]}
		if(stage!=null){
			if(!stgsToProc.containsKey(stage.key)){
				stgsToProc.put(stage.key,stage.value)
				if(stage.value.dependencies!=null)
					addStage(stage.value.dependencies)
			}
			else{
				STAGE_ERR_MSG = "Stage no válida: ${stagesList[i]}"
				exit 0
			}
		}
		else{
			STAGE_ERR_MSG = "Stage no válida: ${stagesList[i]}"
			exit 0
		}
	}
}

return this;

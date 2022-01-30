/*
	forma de invocación de método call:
	def ejecucion = load 'maven.groovy'
	ejecucion.call()
*/

def call(){

	stage('Compile') {
		STAGE = env.STAGE_NAME
		println "Stage: ${env.STAGE_NAME}"
		sh 'mvn clean compile -e'
	}

	stage('Test') {
		STAGE = env.STAGE_NAME
		println "Stage: ${env.STAGE_NAME}"
		sh 'mvn clean test -e'
	}

	stage('Package') {
		STAGE = env.STAGE_NAME
		println "Stage: ${env.STAGE_NAME}"
		sh 'mvn clean package -e'
	}

	stage('Sonar') {
		STAGE = env.STAGE_NAME
		println "Stage: ${env.STAGE_NAME}"
		def scannerHome = tool 'sonar-scanner';
		withSonarQubeEnv('sonar-server') {
			sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-maven -Dsonar.sources=src -Dsonar.java.binaries=build"
		}
	}

	stage('Run') {
		STAGE = env.STAGE_NAME
		println "Stage: ${env.STAGE_NAME}"
		sh 'mvn spring-boot:run &'
		sleep 20
	}

	stage('TestApp') {
		STAGE = env.STAGE_NAME
		println "Stage: ${env.STAGE_NAME}"
		sh "curl -X GET 'http://localhost:8082/rest/mscovid/test?msg=testing'"
	}

	stage('uploadNexus') {
		STAGE = env.STAGE_NAME
		println "Stage: ${env.STAGE_NAME}"
		nexusArtifactUploader artifacts: [[artifactId: 'DevOpsUsach2020', classifier: '', file: '/diplomado/modulo3/ejemplo-maven/build/DevOpsUsach2020-0.0.1.jar', type: 'jar']], credentialsId: 'nexus-taller10', groupId: 'com.devopsusach2020', nexusUrl: 'f5dd-200-126-115-114.ngrok.io', nexusVersion: 'nexus3', protocol: 'http', repository: 'test-nexus', version: '0.0.1'
	}
}

def stageBuild(){
	stage('Compile') {
		STAGE = env.STAGE_NAME
		println "Stage: ${env.STAGE_NAME}"
		sh 'mvn clean compile -e'
	}
}

def stagePackage(){
	stage('Package') {
		STAGE = env.STAGE_NAME
		println "Stage: ${env.STAGE_NAME}"
		sh 'mvn clean package -e'
	}
}

def stageSonar(){
	stage('Sonar') {
		STAGE = env.STAGE_NAME
		println "Stage: ${env.STAGE_NAME}"
		def scannerHome = tool 'sonar-scanner';
		withSonarQubeEnv('sonar-server') {
			sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-maven -Dsonar.sources=src -Dsonar.java.binaries=build"
		}
	}
}

def stageTest(){
	stage('Test') {
		STAGE = env.STAGE_NAME
		println "Stage: ${env.STAGE_NAME}"
		sh 'mvn clean test -e'
	}
}

def stageRun(){
	stage('Run') {
		STAGE = env.STAGE_NAME
		println "Stage: ${env.STAGE_NAME}"
		sh 'mvn spring-boot:run &'
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
	stage('uploadNexus') {
		STAGE = env.STAGE_NAME
		println "Stage: ${env.STAGE_NAME}"
		nexusArtifactUploader artifacts: [[artifactId: 'DevOpsUsach2020', classifier: '', file: '/diplomado/modulo3/ejemplo-maven/build/DevOpsUsach2020-0.0.1.jar', type: 'jar']], credentialsId: 'nexus-taller10', groupId: 'com.devopsusach2020', nexusUrl: 'f5dd-200-126-115-114.ngrok.io', nexusVersion: 'nexus3', protocol: 'http', repository: 'test-nexus', version: '0.0.1'
	}
}

def runMavenStages(stages){
	def map = [1:[name:'build', priority:1, dependencies:null],
			   2:[name:'test', priority:2, dependencies:'build'],
			   3:[name:'package', priority:3, dependencies:'build'],
			   4:[name:'sonar', priority:4, dependencies:'package'],
			   5:[name:'run', priority:5, dependencies:'package'],
			   6:[name:'testapp', priority:0, dependencies:'run'],
			   7:[name:'nexus', priority:7, dependencies:'package']]
	addStage(stages,map,stgsToProc)
	def aux = stgsToProc.sort()
	def keyS = aux.keySet()
	keyS.each {
		def stageName = stgsToProc.get(it).name
		switch(stageName) {
			case "build":
				println "m build: ${stageName}"
				//stageBuild()
				break
			case "test":
				println "m test: ${stageName}"
				//stageTest()
				break
			case "package":
				println "m package: ${stageName}"
				//stagePackage()
				break
			case "sonar":
				println "m sonar: ${stageName}"
				//stageSonar()
				break
			case "run":
				println "m run: ${stageName}"
				//stageRun()
				break
			case "testapp":
				println "m testapp: ${stageName}"
				//stageTestApp()
				break
			case "nexus":
				println "m nexus: ${stageName}"
				//stageNexus()
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

return this;
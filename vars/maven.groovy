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
							map = mavenMap
							addStage(stages)
							def keyS = stgsToProc.sort { a, b -> a.value.priority <=> b.value.priority }.keySet()
							keyS.each {
								def stageName = stgsToProc.get(it).name
								switch(stageName) {
									case "build":
										println "m build: ${stageName}"
										break
									case "sonar":
										println "m sonar: ${stageName}"
										break
									case "run":
										println "m run: ${stageName}"
										break
									case "testapp":
										println "m testapp: ${stageName}"
										break
									case "nexus":
										println "m nexus: ${stageName}"
										break
									default:
										STAGE_ERR_MSG = "Stage no válida: ${stagesList[i]}"
										println STAGE_ERR_MSG
										break
								}
							}
						}

return this;
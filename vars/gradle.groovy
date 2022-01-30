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
	map = gradleMap
	addStage(stages)
	def keyS = stgsToProc.sort { a, b -> a.value.priority <=> b.value.priority }.keySet()
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

return this;

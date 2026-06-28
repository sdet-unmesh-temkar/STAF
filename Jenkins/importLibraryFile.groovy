library identifier: 'shared-jenkins-library@main',
        retriever: modernSCM([
                $class: 'GitSCMSource',
                credentialsId: 'githubapp_STAF',
                remote: 'https://github.vodafone.com/VFDE-Solstice-TestAutomation/shared-jenkins-library.git'
        ])
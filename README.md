# optimistic-lock-based-list-based-set
This is a java project in which an optimistic lock-based list is implemented in Java and then tested using the synchrobench framework.

# Contents
- [How to setup the project](#how-to-run-this-project)
- [Experiment runs on VMs](#remote-runs-infres)
- [Project structure](#project-structure)
- [Credits](#people-involved)
- [License](#license)

# How to setup this project
1. Install depdendencies
```
sudo apt-get install make
sudo apt-get install git-lfs
```
2. Pull with git lfs in order to populate deps/ folder
```
git lfs pull
```
3. Open a terminal in code/ folder and run
```
source env-setup.sh
```
4. Run make & make check, wait for success
```
Oct 26, 2025 2:37:07 PM org.deuce.transform.asm.Agent transformJar
INFO: Start tranlating source: optimistic-lock-based-list-based-set/deps/jdk1.7.0_80/jre/lib/rt.jar target:lib/rt_instrumented.jar
Oct 26, 2025 2:37:18 PM org.deuce.transform.asm.Agent transformJar
INFO: Closing source:/optimistic-lock-based-list-based-set/deps/jdk1.7.0_80/jre/lib/rt.jar target:lib/rt_instrumented.jar
```
5. Profit!

# Remote runs INFRES
- Setup the VPN connection (Linux) as stated below:
```
https://eole.telecom-paris.fr/vos-services/services-numeriques/connexions-aux-reseaux/openvpn-avec-debian-gnulinux
```
- Use Remmina to setup a ssh connexion to one of the machines from
```
# install remmina
sudo apt install remmina

# site to check machines
https://lames.enst.fr/grafana/d/3rE8q-FZk/dashboard-infres?orgId=2
```
- Clone the repo on the remote machine and follow the steps on the topic above
- Use ```test-script.sh``` from code\ to start performing the tests

! ```If needed, use scp to move the deps archives to the remote machine``` !

# Project structure
```
.
├── code                // synchrobench java code
│   ├── ...
│   ├── env-setup.sh    // script to setup environment before runs
│   ├── lib
│   ├── Makefile
│   ├── README.md
│   ├── src             // Java implementation of shared objects
│   └── test-script.sh  // script to run experiments
├── deps                // cannot be installed with apt
│   ├── apache-ant-1.9.15-bin.tar.gz
│   └── jdk-7u80-linux-x64.tar.gz
├── docs                // references
│   ├── project_description.pdf
│   ├── report          // final report dir with pdf & docx
│   ├── Theartofmulticore.pdf
│   └── Tips_SLR206_Project.pdf
├── LICENSE
└── README.md
```

# People involved
[Elena](https://github.com/lena0097) and me.

# License
Apache License Version 2.0
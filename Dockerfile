FROM ubuntu:22.04

#############################################################################
# Setup base image 
#############################################################################
RUN \
  apt-get update -y && \
  apt-get install software-properties-common -y && \
  apt-get install apt-utils -y && \
  apt-get install -y openjdk-11-jdk  && \
  apt-get install -y openjdk-8-jdk && \
  apt-get update -y && \
  apt-get install -y ant && \
  apt-get install -y git \
                build-essential \
                python2 \
                python3 \
                wget \
                antlr3 \
                && \
  rm -rf /var/lib/apt/lists/* 

#############################################################################
# Environment 
#############################################################################

# Set java env
ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64

# Home dir
RUN mkdir /home/project
ENV PROJ_DIR /home/project
WORKDIR ${PROJ_DIR}

# Install objdump
RUN apt-get update -y
RUN apt-get install binutils-arm-none-eabi

# Install Z3 
RUN apt-get update -y && apt-get install -y z3

# Clone github
#RUN git clone https://github.com/vananhnt/HybridSE.git
COPY . ${HYBRIDSE}
ENV HYBRIDSE ${PROJ_DIR}/HybridSE/src
ENV CORANA ${HYBRIDSE}/corana

# Install mongodb
WORKDIR ${PROJ_DIR}
RUN wget -qO - https://www.mongodb.org/static/pgp/server-4.4.asc | apt-key add -
RUN echo "deb [ arch=amd64,arm64 signed-by=/usr/share/keyrings/mongodb-server-7.0.gpg ] https://repo.mongodb.org/apt/ubuntu jammy/mongodb-org/7.0 multiverse" | tee /etc/apt/sources.list.d/mongodb-org-7.0.list
RUN apt-get update
RUN apt-get install -y mongodb-org
RUN systemctl start mongod

#RUN echo "jpf-core = ${user.home}/.../path-to-jpf-core-folder/jpf-core" | tee -a ${PROJ_DIR}/.site.properties
#RUN echo "jpf-symbc = ${user.home}/.../path-to-jpf-core-folder/jpf-symbc" | tee -a ${PROJ_DIR}.site.properties
#RUN echo "extensions={jpf-symbc}" | tee -a ${PROJ_DIR}.site.properties


#WORKDIR ${CORANA}/lib/capstone
#RUN ./make.sh 
#RUN ./make.sh install

#WORKDIR ${HYBRIDSE}
#RUN bash build.sh ${HYBRIDSE}


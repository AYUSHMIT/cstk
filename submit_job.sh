#!/bin/bash
#SBATCH --time 8:0:0
#SBATCH --account=def-jinguo
#SBATCH --gres=gpu:a100:1
#SBATCH --mem=40G
#SBATCH --cpus-per-task=24

export LD_LIBRARY_PATH=/cvmfs/soft.computecanada.ca/easybuild/software/2017/Core/cudacore/10.2.89/targets/x86_64-linux/lib/
export TRANSFORMERS_OFFLINE=1
module load python/3.8
module load java
source bin/activate
./gradlew --offline completeCode | tee logfile.txt
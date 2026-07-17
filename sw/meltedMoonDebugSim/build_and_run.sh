#!/bin/bash

TRACE=0
RISCV_CPU_DEBUG=0
RISCV_CPU_PROFILE=0

if (($# != 0)); then
    if (($1 == 1)); then
        TRACE=1
    elif (($1 == 2)); then
        #TRACE=0
        RISCV_CPU_DEBUG=1
    elif (($1 == 3)); then
        TRACE=1
        RISCV_CPU_DEBUG=1
    fi

fi

if (($# > 1)); then
    if (($2 == 1)); then
        RISCV_CPU_PROFILE=1
    fi
fi

#if ((TRACE == 0)); then
#    make clean && make verilate && make -j12 --output-sync && ./meltedMoonDebugSim
#else
#    make clean && make TRACE=1 verilate && make TRACE=1 -j12 --output-sync && ./meltedMoonDebugSim
#fi
MY_MAKE_FLAGS=""
MY_MAKE_FLAGS="$MY_MAKE_FLAGS TRACE=$TRACE"
MY_MAKE_FLAGS="$MY_MAKE_FLAGS RISCV_CPU_DEBUG=$RISCV_CPU_DEBUG"
MY_MAKE_FLAGS="$MY_MAKE_FLAGS RISCV_CPU_PROFILE=$RISCV_CPU_PROFILE"

DO_CLEAN_FIRST=1

if (($# > 2)); then
    if (($3 == 0)); then
        DO_CLEAN_FIRST=0
    fi
fi

if (($DO_CLEAN_FIRST != 0)); then
    make clean \
    && make $MY_MAKE_FLAGS verilate \
    && make $MY_MAKE_FLAGS -j12 --output-sync \
    && ./meltedMoonDebugSim
else
    make $MY_MAKE_FLAGS -j12 --output-sync \
    && ./meltedMoonDebugSim
fi

#
##make verilate && make -j18
###numactl -m 0 -C 0,1,2,3 -m 1 -C 4,5,6,7 -- make verilate 
###numactl --physcpubind=+0-4,8-12 -- 
###./build.sh
###make verilate 
##retval=$?
##if (( $retval == 0 )); then
##	#make -j8
##	##./gpu2dSim
##	#numactl -m 0 -C 0,1,2,3,4,5,6,7 -- ./gpu2dSim
##	#./gpu2dSim
##	#./snowHouseCpuFbDemoSim
##	./meltedMoonDebugSim
##	#numactl -m 2 -C 0,1,2,3 -- ./gpu2dSim
##	#numactl -m 0 -C 0,1,2,3 -- ./gpu2dSim
##	#numactl -m 0 -C 0,2,4,6 -- ./gpu2dSim
##	#numactl -m 0 -C 0,1,2,3 -m 1 -C 4,5,6,7 -- ./gpu2dSim
##fi

#!/bin/bash

#for file in *.v; do echo '/*verilator tracing_off*/' >> tempfile.txt; cat tempfile.txt $file > tempfile1.txt; cat tempfile1.txt ; done

cd src

for file in ../../../hw/gen/meltedMoonDebug/*.v; do
	rm $(basename $file)
	ln -s $file
done

echo '/*verilator tracing_off*/' > tempfile_donttouch.txt

#my_tracing_off_arr=(StreamFifo*.v WrPulse*.v RamSdpPipe*.v LcvBusDoStallFifoThing_1.v LcvVgaCtrl.v LcvBusH2dShiftedDataEtcStreamAdapter.v LcvBusDoStallFifoThing.v LcvBusSdramCtrl.v CalcLcvBusShiftedDataEtc_1.v LcvBusDeburster.v LcvBusD2hShiftedDataEtcStreamAdapter.v LcvBusArbiter.v CalcLcvBusShiftedDataEtc.v LcvBusSlicer.v LcvBusFramebufferCtrl.v LcvBusArbiter_1.v FpgacpuRamSimpleDualPort_1.v FpgacpuRamSimpleDualPort_2.v FpgacpuRamSimpleDualPort_3.v FpgacpuRamSimpleDualPort_4.v RamSimpleDualPort.v RamSimpleDualPort_1.v RamSimpleDualPort_3.v RamSimpleDualPort_4.v)

#my_tracing_off_arr=( CalcLcvBusShiftedDataEtc.v CalcLcvBusShiftedDataEtc_1.v FpgacpuRamSimpleDualPort_1.v FpgacpuRamSimpleDualPort_2.v FpgacpuRamSimpleDualPort_3.v FpgacpuRamSimpleDualPort_4.v LcvBusArbiter.v LcvBusArbiter_1.v LcvBusCache.v LcvBusD2hShiftedDataEtcStreamAdapter.v LcvBusDeburster.v LcvBusDoStallFifoThing.v LcvBusDoStallFifoThing_1.v LcvBusFramebufferCtrl.v LcvBusH2dShiftedDataEtcStreamAdapter.v LcvBusNonCoherentDataCache.v LcvBusSdramCtrl.v LcvBusSlicer.v LcvVgaCtrl.v LongDivMultiCycle.v RamSdpPipe.v RamSdpPipeImpl.v RamSdpPipeImpl_1.v RamSdpPipeImpl_2.v RamSdpPipeImpl_3.v RamSdpPipeImpl_4.v RamSdpPipeImpl_5.v RamSdpPipeImpl_6.v RamSdpPipeImpl_7.v RamSdpPipeImpl_8.v RamSdpPipe_1.v RamSdpPipe_2.v RamSdpPipe_3.v RamSdpPipe_4.v RamSdpPipe_5.v RamSdpPipe_6.v RamSdpPipe_7.v RamSdpPipe_8.v RamSimpleDualPort.v RamSimpleDualPort_1.v RamSimpleDualPort_3.v RamSimpleDualPort_4.v SnowHouseAsrDel1.v SnowHouseCpuMulFullProduct.v SnowHouseLslDel1.v SnowHouseLsrDel1.v StreamFifo_1.v StreamFifo_11.v StreamFifo_16.v StreamFifo_17.v StreamFifo_2.v StreamFifo_3.v StreamFifo_5.v StreamFifo_6.v StreamFifo_7.v StreamFifo_8.v StreamFifo_9.v WrPulseRdPipeRamSdpPipe.v WrPulseRdPipeRamSdpPipe_1.v WrPulseRdPipeRamSdpPipe_2.v)

my_tracing_off_arr=(
    LcvBusNonCoherentDataCache_1.v
    #RamSimpleDualPort_2.v
    RamSdpPipeImpl_4.v
    RamSdpPipeImpl.v
    StreamFifo_2.v
    FpgacpuRamSimpleDualPort_4.v
    LongDivMultiCycle.v
    RamSdpPipe_7.v
    StreamFifo_17.v
    LcvBusDoStallFifoThing_1.v
    LcvBusIrqCtrl.v
    RamSdpPipeImpl_3.v
    StreamFifo_3.v
    StreamFifo_9.v
    LcvBusCache_2.v
    RamSdpPipe_2.v
    StreamFifo_16.v
    #SnowHouseCpuWithoutRam.v
    enumdefine.v
    #MeltedMoonSimDut.v
    RamSimpleDualPort.v
    RamSdpPipe_4.v
    LcvBusH2dShiftedDataEtcStreamAdapter.v
    #SnowHousePipeStageExecuteSetOutpModMemWord.v
    LcvVgaCtrl.v
    LcvBusDoStallFifoThing.v
    LcvBusSdramCtrl.v
    RamSdpPipeImpl_2.v
    SnowHouseLsrDel1.v
    RamSdpPipe_5.v
    CalcLcvBusShiftedDataEtc_1.v
    StreamFifo_6.v
    WrPulseRdPipeRamSdpPipe_2.v
    #SnowHouseBranchPredictor.v
    RamSimpleDualPort_3.v
    StreamFifo_8.v
    WrPulseRdPipeRamSdpPipe.v
    RamSdpPipe_1.v
    LcvBusDeburster.v
    RamSdpPipe_8.v
    #SnowHouse.v
    WrPulseRdPipeRamSdpPipe_1.v
    FpgacpuRamSimpleDualPort_1.v
    StreamFifo_7.v
    RamSimpleDualPort_4.v
    RamSdpPipe.v
    RamSdpPipeImpl_8.v
    LcvBusD2hShiftedDataEtcStreamAdapter.v
    LcvBusArbiter.v
    LcvBusCache_1.v
    LcvBusNonCoherentDataCache.v
    CalcLcvBusShiftedDataEtc.v
    RamSdpPipeImpl_5.v
    LcvBusNonCoherentInstrCache.v
    SnowHouseLslDel1.v
    RamSdpPipeImpl_1.v
    LcvBusSlicer.v
    #MeltedMoon.v
    StreamFifo_5.v
    #MeltedMoonDbgPrint.v
    SnowHouseAsrDel1.v
    SnowHouseCpuMulFullProduct.v
    LcvBusCache.v
    #FpgacpuRamSimpleDualPort.v
    RamSdpPipeImpl_6.v
    RamSdpPipe_6.v
    LcvBusFramebufferCtrl.v
    RamSdpPipeImpl_7.v
    FpgacpuRamSimpleDualPort_3.v
    RamSdpPipe_3.v
    StreamFifo_11.v
    LcvBusArbiter_1.v
    FpgacpuRamSimpleDualPort_2.v
    RamSimpleDualPort_1.v
    StreamFifo_1.v
)

for file in ${my_tracing_off_arr[@]}; do
    #echo $file
    cat tempfile_donttouch.txt "$file" > tempfile_donttouch1.txt
    cat tempfile_donttouch1.txt > "$file"
    rm tempfile_donttouch1.txt
done

rm tempfile_donttouch.txt 

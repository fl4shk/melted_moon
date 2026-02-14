package melted_moon

import spinal.core._
import spinal.core.sim._
import spinal.lib._
import spinal.core.formal._
import spinal.lib.misc.pipeline._
import spinal.lib.io._

import spinal.lib.graphic.vga._
import spinal.lib.graphic.Rgb
import spinal.lib.graphic.RgbConfig

import scala.collection.immutable
import scala.collection.mutable._

import libcheesevoyage.bus.lcvBus._
import libcheesevoyage.general._
import libcheesevoyage.gfx._
import libcheesevoyage.math._
import libcheesevoyage.bus.lcvBus._
import libsnowhouse._


//////////////////////////////////////////////////////////
// DW:
//  6 : 2R 2G 2B
//  8 : 3R 3G 2B
//  9 : 3R 3G 3B
// 12 : 4R 4G 4B
// 24 : 8R 8G 8B

//module arcade_video #(parameter WIDTH=320, DW=8, GAMMA=1)
//(
//	input         clk_video,
//	input         ce_pix,
//
//	input[DW-1:0] RGB_in,
//	input         HBlank,
//	input         VBlank,
//	input         HSync,
//	input         VSync,
//
//	output        CLK_VIDEO,
//	output        CE_PIXEL,
//	output  [7:0] VGA_R,
//	output  [7:0] VGA_G,
//	output  [7:0] VGA_B,
//	output        VGA_HS,
//	output        VGA_VS,
//	output        VGA_DE,
//	output  [1:0] VGA_SL,
//
//	input   [2:0] fx,
//	input         forced_scandoubler,
//	inout  [21:0] gamma_bus
//);

//case class ArcadeVideoConfig(
//  width: Int=320,
//  dw: Int=12,
//  gamma: Int=1,
//) {
//}


case class MeltedMoonConfig(
  //sdramCfg: LcvBusSdramCtrlConfig=LcvBusSdramCtrlConfig(
  //  clkRate=100.0 MHz
  //),
  sdramCtrlUseAltddioOut: Boolean=true,
) {
  val clkRate = 100.0 MHz
  val cpuCfg = SnowHouseCpuConfig(
    optFormal=false,
    targetAltera=true,
    //exposeModMemWordToIo=(
    //  //false
    //  true
    //),
    instrRamKind=0,
    programStr=(
      //"test/snowhousecpu-test-5.bin"
      "test/snowhousecpu-framebuffer-demo.bin"
    ),
    exposeRegFileWriteDataToIo=true,
    regFileMemRamStyleAltera=(
      //"no_rw_check, logic"
      "no_rw_check, MLAB"
      //"no_rw_check, M10K"
      //"auto"
      //"M144K"
      //"no_rw_check"
    ),
    icacheLineWordMemRamStyleAltera=(
      //"no_rw_check, MLAB"
      "no_rw_check, M10K"
    ),
    icacheLineAttrsMemRamStyleAltera=(
      //"no_rw_check, MLAB"
      //"no_rw_check, logic"
      "no_rw_check, M10K"
    ),
    dcacheLineWordMemRamStyleAltera=(
      //"no_rw_check, MLAB"
      "no_rw_check, M10K"
      //"MLAB"
    ),
    dcacheLineAttrsMemRamStyleAltera=(
      "no_rw_check, MLAB"
      //"no_rw_check, M10K"
      //"no_rw_check, logic"
    ),
  )
  //val cfg = SnowHouseCpuConfig(
  //  optFormal=(
  //    //true
  //    false
  //  ),
  //  exposeModMemWordToIo=true,
  //)
  val testProgram = SnowHouseCpuTestProgram(cfg=cpuCfg)
  //SnowHouseCpuWithDualRam(program=testProgram.program)
  val demoCfg = SnowHouseCpuFramebufferDemoConfig(
    program=testProgram.program,
    clkRate=clkRate,
    rgbCfg=RgbConfig(rWidth=8, gWidth=8, bWidth=8),
    vgaTimingInfo=LcvVgaTimingInfoMap.map("640x480@60"),
    fbCnt2dShift=ElabVec2[Int](
      x=1,
      y=1,
    )
  )
  val sdramCtrlCfg = LcvBusSdramCtrlConfig(
    clkRate=clkRate,
    useAltddioOut=sdramCtrlUseAltddioOut,
    srcWidth=cpuCfg.shCfg.subCfg.myLcvBusSrcWidth
  )
  val ioctlWide = 1;
  val ioctlSpinalDw = (
    if (ioctlWide != 0) (16) else (8)
  )
}

//case class MeltedMoonMisterIoctl(
//  cfg: MeltedMoonConfig,
//) extends Bundle {
//  def DW = cfg.ioctlSpinalDw - 1
//	// ARM -> FPGA download
//	val ioctl_download = in(Bool()) // signal indicating an active download
//	val ioctl_index = in(UInt(15 + 1 bits))
//	  // menu index used to upload the file
//	val ioctl_wr = in(Bool())
//	val ioctl_addr = in(UInt(26 + 1 bits))
//	  // in WIDE mode address will be incremented by 2
//	val ioctl_dout = in(UInt(DW + 1 bits))
//	val ioctl_upload = in(Bool()) // signal indicating an active upload
//	val ioctl_upload_req = out(Bool())
//	  // request to save (must be supported on HPS side for specific core)
//	val ioctl_upload_index = out(UInt(7 + 1 bits))
//	val ioctl_din = out(UInt(DW + 1 bits))
//	val ioctl_rd = in(Bool())
//	val ioctl_file_ext = in(UInt(31 + 1 bits))
//	val ioctl_wait = out(Bool()) // rename this to `wait`
//}

case class MeltedMoonIo(
  //clkRate: HertzNumber,
  cfg: MeltedMoonConfig,
) extends Bundle {
  //--------
  val mainLogicReset = in(Bool())
  //--------
  val sdram = LcvBusSdramIo(
    cfg=cfg.sdramCtrlCfg
  )
  //--------
  def ioctlDW = cfg.ioctlSpinalDw - 1
	// ARM -> FPGA download
	val ioctl_download = in(Bool()) // signal indicating an active download
	val ioctl_index = in(UInt(15 + 1 bits))
	  // menu index used to upload the file
	val ioctl_wr = in(Bool())
	val ioctl_addr = in(UInt(26 + 1 bits))
	  // in WIDE mode address will be incremented by 2
	val ioctl_dout = in(UInt(ioctlDW + 1 bits))
	val ioctl_upload = in(Bool()) // signal indicating an active upload
	val ioctl_upload_req = out(Bool())
	  // request to save (must be supported on HPS side for specific core)
	val ioctl_upload_index = out(UInt(7 + 1 bits))
	val ioctl_din = out(UInt(ioctlDW + 1 bits))
	val ioctl_rd = in(Bool())
	val ioctl_file_ext = in(UInt(31 + 1 bits))
	val ioctl_wait = out(Bool()) // rename this to `wait`
	//--------
	val vgaPhys = out(LcvVgaPhys(rgbConfig=cfg.demoCfg.rgbCfg))
	val vgaPixelEn = out(Bool())
	val vgaVisib = out(Bool())
	//--------
}

case class MeltedMoon(
  cfg: MeltedMoonConfig,
) extends Component {
  //--------
  val io = MeltedMoonIo(cfg=cfg)
  noIoPrefix()
  //--------
  //val ioctlClkDomain = ClockDomain.external(
  //  name="ioctlClk",
  //  withReset=true,
  //  frequency=FixedFrequency(
  //    cfg.demoCfg.clkRate
  //  )
  //)
  //val mainClkDomain = ClockDomain.internal(
  //  name="mainClk",
  //  config=(
  //    ClockDomain.current.config
  //    //Config.spinalWithFreq(
  //    //  cfg.demoCfg.clkRate
  //    //).defaultConfigForClockDomains
  //  ),
  //  withReset=true,
  //)
  //mainClkDomain.clock := ClockDomain.current.readClockWire
  //mainClkDomain.reset := io.mainLogicReset

  val vgaClkDomain = ClockDomain.external(
    name="vgaClk",
    withReset=true,//false,
    frequency=FixedFrequency(
      //25.0 MHz
      //cfg.demoCfg.vgaTimingInfo.pixelClk
      cfg.demoCfg.clkRate
    ),
  )
  val pixelFifo = StreamFifoCC(
    dataType=Rgb(cfg.demoCfg.rgbCfg),
    depth=(
      //io.misc.fifoDepth
      16
    ),
    pushClock=(
      ClockDomain.current
      //mainClkDomain
    ),
    popClock=vgaClkDomain,
  )
  val vblankIrqFifo = StreamFifoCC(
    dataType=Bool(),
    depth=(
      4
    ),
    pushClock=vgaClkDomain,
    popClock=(
      ClockDomain.current
      //mainClkDomain
    ),
  )
  def cpp = LcvVgaCtrl.cpp(
    clkRate=cfg.clkRate,
    vgaTimingInfo=cfg.demoCfg.vgaTimingInfo,
  )
  println(
    s"here we go: cpp:${cpp}"
  )
  val vgaClockingArea = new ClockingArea(vgaClkDomain) {
    val vgaCtrl = VgaCtrl(rgbConfig=cfg.demoCfg.rgbCfg)

    val vgaTimingInfo = cfg.demoCfg.vgaTimingInfo
    //if (vgaTimingInfo == LcvVgaTimingInfoMap.map("640x480@60")) {
    //  vgaCtrl.io.timings.setAs_h640_v480_r60
    //} else if (vgaTimingInfo == LcvVgaTimingInfoMap.map("1920x1080@60")) {
    //  vgaCtrl.io.timings.setAs_h1920_v1080_r60
    //} else {
      // TODO: check if this works?
      vgaTimingInfo.driveSpinalVgaTimings(
        clkRate=cfg.clkRate,
        spinalVgaTimings=vgaCtrl.io.timings,
      )
    //}

    //val lcvVgaCtrl = (
    //  LcvVgaCtrl(
    //    clkRate=cfg.clkRate,
    //    //rgbConfig=physRgbConfig,
    //    rgbConfig=cfg.rgbCfg,
    //    vgaTimingInfo=cfg.vgaTimingInfo,
    //    fifoDepth=(
    //      //cfg.ctrlFifoDepth
    //      io.misc.fifoDepth
    //    ),
    //  )
    //)
    //io.phys := lcvVgaCtrl.io.phys
    //io.misc := lcvVgaCtrl.io.misc
    //lcvVgaCtrl.io.en := True

    //--------
    when (vgaCtrl.io.vga.colorEn) {
      io.vgaPhys.col := vgaCtrl.io.vga.color
    } otherwise {
      io.vgaPhys.col := io.vgaPhys.col.getZero
    }
    io.vgaPhys.hsync := vgaCtrl.io.vga.hSync
    io.vgaPhys.vsync := vgaCtrl.io.vga.vSync
    //io.misc := io.misc.getZero
    //io.misc.allowOverride
    //io.misc.pastVisib := RegNext(io.misc.visib) init(False)
    io.vgaVisib := vgaCtrl.io.vga.colorEn
    //io.misc.pixelEn := (
    //  True
    //)
    vgaCtrl.io.softReset := RegNext(False) init(True)
    //vgaCtrl.io.pixels <-/< myFbCtrl.io.pop
    vgaCtrl.io.pixels <-/< pixelFifo.io.pop.repeat(
      times=cpp
    )._1

    val myDoVblankIrq = Bool()
    val rSavedDoVblankIrq = Reg(Bool(), init=False)
    val stickyDoVblankIrq = (
      myDoVblankIrq
      || rSavedDoVblankIrq
    )
    when (myDoVblankIrq) {
      rSavedDoVblankIrq := True
    }
    when (vblankIrqFifo.io.push.fire) {
      rSavedDoVblankIrq := False
    }
    myDoVblankIrq := (
      rose(
        RegNext(
          //!io.vgaVisib
          (
            !io.vgaPhys.vsync
            && !io.vgaPhys.hsync
          ),
          init=False
        )
      )
    )
    vblankIrqFifo.io.push.valid := stickyDoVblankIrq
    vblankIrqFifo.io.push.payload := True
  }

  object MyIrqState
  extends SpinalEnum(defaultEncoding=binaryOneHot) {
    val
      IDLE,
      VBLANK
      = newElement();
  }
  val mySdramCtrl = LcvBusSdramCtrl(
    cfg=cfg.sdramCtrlCfg
  )
  mySdramCtrl.io.sdram <> io.sdram

  def mySdramCtrlHostIdxIoctl = 0
  def mySdramCtrlHostIdxFbDcache = 1
  def mySdramCtrlHostIdxIcache = 2
  def mySdramCtrlHostIdxNonFbDcache = 3
  def limMySdramCtrlHostIdx = 4

  val mySdramCtrlBusArbiter = LcvBusArbiter(
    cfg=LcvBusArbiterConfig(
      busCfg=cfg.sdramCtrlCfg.busCfg,
      numHosts=limMySdramCtrlHostIdx, // add 1 for the icache
      kind=LcvBusArbiterKind.Priority,
    )
  )
  mySdramCtrl.io.bus <-/< mySdramCtrlBusArbiter.io.dev
  def mySdramCtrlIoctlHost = (
    mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxIoctl)
  )
  def mySdramCtrlFbDcacheHost = (
    mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxFbDcache)
  )
  def mySdramCtrlIcacheHost = (
    mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxIcache)
  )
  def mySdramCtrlNonFbDcacheHost = (
    mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxNonFbDcache)
  )
  val sdramInitFifo = StreamFifo(
    dataType=cloneOf(mySdramCtrlIoctlHost.h2dBus.payload),
    depth=(
      32,
    ),
    latency=2,
    forFMax=true,
    //pushClock=ioctlClkDomain,
    //popClock=ClockDomain.current,
  )
  //val ioctlClockingArea = new ClockingArea(ioctlClkDomain) 
  val ioctlArea = new Area {
    io.ioctl_upload_req := False
    io.ioctl_upload_index := 0x0
    io.ioctl_din := 0x0
    //io.ioctl_wait := False

    case class MyIoctlPayload(
      dataWidth: Int,
    ) extends Bundle {
      val addr = (
        //cloneOf(io.ioctl_addr)
        UInt(cfg.sdramCtrlCfg.busCfg.addrWidth bits)
      )
      val data = UInt(dataWidth bits)
    }

    val myIoctlRecvFifo = StreamFifo(
      dataType=MyIoctlPayload(
        cfg.sdramCtrlCfg.busCfg.dataWidth
        //cfg.ioctlSpinalDw bits
      ),
      depth=8,
      latency=2,
      forFMax=true,
    )
    val myIoctlRecvPushStm = (
      Vec[Stream[MyIoctlPayload]](
        List[Stream[MyIoctlPayload]](
          Stream(MyIoctlPayload(cfg.ioctlSpinalDw)),
          Stream(MyIoctlPayload(cfg.sdramCtrlCfg.busCfg.dataWidth)),
        )
      )
      //cloneOf(myIoctlRecvFifo.io.push)
      //Stream(
      //  UInt(
      //    cfg.sdramCtrlCfg.busCfg.dataWidth bits
      //    //cfg.ioctlSpinalDw bits
      //  )
      //)
    )
    val rIoctlPushCnt = (
      Reg(UInt(
        //1 bits
        log2Up(
          cfg.sdramCtrlCfg.busCfg.dataWidth
          / cfg.ioctlSpinalDw
        ).toInt
        bits
      )) init(0x0)
    )
    myIoctlRecvPushStm.head.translateInto(myIoctlRecvPushStm.last)(
      dataAssignment=(outp, inp) => {
        outp.addr := inp.addr
        outp.data := outp.data.getZero
        switch (rIoctlPushCnt) {
          for (idx <- 0 until (1 << rIoctlPushCnt.getWidth)) {
            is (idx) {
              println(
                (idx + 1) * inp.data.getWidth - 1
                downto idx * inp.data.getWidth
              )
              outp.data(
                (idx + 1) * inp.data.getWidth - 1
                downto idx * inp.data.getWidth
              ) := inp.data
            }
          }
        }
      }
    )
    myIoctlRecvFifo.io.push <-/< myIoctlRecvPushStm.last
    when (myIoctlRecvPushStm.last.fire) {
      rIoctlPushCnt := rIoctlPushCnt + 1
    }
    myIoctlRecvPushStm.head.valid := (
      //io.ioctl_upload 
      io.ioctl_wr && io.ioctl_download
    )
    myIoctlRecvPushStm.head.data := io.ioctl_dout
    myIoctlRecvPushStm.head.addr := (
      io.ioctl_addr.resize(myIoctlRecvPushStm.head.addr.getWidth)
    )
    io.ioctl_wait := !myIoctlRecvPushStm.head.ready

    val myIoctlRecvPopStm = cloneOf(myIoctlRecvFifo.io.pop)
    myIoctlRecvPopStm <-/< myIoctlRecvFifo.io.pop
    myIoctlRecvPopStm.translateInto(
      //mySdramCtrlBusArbiter.io.hostVec.head.h2dBus
      //mySdramCtrlIoctlHost.h2dBus
      sdramInitFifo.io.push
    )(
      dataAssignment=(outp, inp) => {
        outp.addr := (
          Cat(
            inp.addr(inp.addr.high downto 2),
            U"2'b00",
          ).asUInt
        )
        outp.data := inp.data
        outp.isWrite := True
        when (
          inp.addr(2)
          === RegNextWhen(
            myIoctlRecvPopStm.addr(2),
            cond=myIoctlRecvPopStm.fire,
            init=myIoctlRecvPopStm.addr(2).getZero
          )
          //|| !History[Bool](
          //  that=True,
          //  when=myIoctlRecvPopStm.fire,
          //  length=2,
          //  init=False,
          //).last
        ) {
          outp.byteEn := 0xc
        } otherwise {
          outp.byteEn := 0x3
        }
        //outp.byteEn := U(outp.byteEn.getWidth bits, default -> True)
        outp.src := outp.src.getZero

        outp.burstCnt := outp.burstCnt.getZero
        outp.burstFirst := False
        outp.burstLast := False
      }
    )
    //mySdramCtrlIoctlHost.d2hBus.ready := False
    //mySdramCtrlBusArbiter.io.hostVec.head.d2hBus.ready := True

    //mySdramCtrlBusArbiter.io.hostVec(1).h2dBus.valid := False
    //mySdramCtrlBusArbiter.io.hostVec(1).h2dBus.payload := (
    //  mySdramCtrlBusArbiter.io.hostVec(1).h2dBus.payload.getZero
    //)
    //mySdramCtrlBusArbiter.io.hostVec(1).d2hBus.ready := False

    //mySdramCtrlBusArbiter.io.hostVec.last.h2dBus.valid := False
    //mySdramCtrlBusArbiter.io.hostVec.last.h2dBus.payload := (
    //  mySdramCtrlBusArbiter.io.hostVec.last.h2dBus.payload.getZero
    //)
    //mySdramCtrlBusArbiter.io.hostVec.last.d2hBus.ready := False
    mySdramCtrlIoctlHost.h2dBus <-/< sdramInitFifo.io.pop
    mySdramCtrlIoctlHost.d2hBus.ready := True
  }

  val main = new ResetArea(io.mainLogicReset, false) {
    val rPixelEnCnt = Reg(UInt(
      log2Up(cpp) bits
    ))
    io.vgaPixelEn := (
      rPixelEnCnt === cpp - 1
    )
    when (rPixelEnCnt < cpp - 1) {
      rPixelEnCnt := rPixelEnCnt + 1
    } otherwise {
      rPixelEnCnt := 0x0
    }
    val myFbCtrl = LcvBusFramebufferCtrl(
      cfg=(
        cfg.demoCfg.myFbCfg
        //myDbgFbCfg
      )
    )
    pixelFifo.io.push <-/< myFbCtrl.io.pop
    //--------
    val cpu = SnowHouseCpuWithoutRam(program=cfg.testProgram.program)

    val rMyIrqState = (
      Reg(MyIrqState())
      init(MyIrqState.IDLE)
    )
    //cpu.io.idsIraIrq.nextValid
    val rIrqValid = Reg(Bool(), init=False)
    cpu.io.idsIraIrq.nextValid := (
      rIrqValid
      //RegNext(
      //  cpu.io.idsIraIrq.nextValid,
      //  init=cpu.io.idsIraIrq.nextValid.getZero
      //)
    )
    vblankIrqFifo.io.pop.ready := False

    switch (rMyIrqState) {
      is (MyIrqState.IDLE) {
        when (
          //rose(
          //  RegNext(
          //    lcvVgaCtrl.io.misc.vpipeS =/= LcvVgaState.visib,
          //    init=False
          //  )
          //)
          //rose(
          //  RegNext(
          //    //!io.vgaVisib
          //    (
          //      !io.vgaPhys.vsync
          //      && !io.vgaPhys.hsync
          //    ),
          //    init=False
          //  )
          //)
          vblankIrqFifo.io.pop.valid
        ) {
          vblankIrqFifo.io.pop.ready := True
          rMyIrqState := MyIrqState.VBLANK
          rIrqValid := True
        }
      }
      is (MyIrqState.VBLANK) {
        //cpu.io.idsIraIrq 
        when (
          //RegNext(
          //  cpu.io.idsIraIrq.nextValid
          //  init=False
          //)
          rIrqValid
          && cpu.io.idsIraIrq.ready
        ) {
          rIrqValid := False
          rMyIrqState := MyIrqState.IDLE
        }
      }
    }
    //--------

    //def mySdramCtrlHostIdxIoctl = 0
    //def mySdramCtrlHostIdxFbCtrl = 1
    ////def mySdramCtrlHostIdxCpuBypassCache = 2
    ////def mySdramCtrlHostIdxCpuCached = 3
    //def mySdramCtrlHostIdxCpu = 2
    //def limMySdramCtrlHostIdx = 3

    val icache = LcvBusCache(cfg=cfg.cpuCfg.shCfg.subCfg.lcvIbusEtcCfg)

    def myDcacheArrIdxFb = 0
    def myDcacheArrIdxNonFb = 1
    def limMyDcacheArrIdx = 2
    val dcacheArr = Array.fill(limMyDcacheArrIdx)(
      LcvBusCache(cfg=cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg)
    )
    def myFbDcache = dcacheArr(myDcacheArrIdxFb)
    def myNonFbDcache = dcacheArr(myDcacheArrIdxNonFb)

    //mySdramCtrl.io.bus <-/< mySdramCtrlBusArbiter.io.dev

    val myDcacheSlicer = LcvBusSlicer(
      cfg=LcvBusSlicerConfig(
        mmapCfg=(
          cfg.demoCfg.myFbDbusSlicerMmapCfg
        ),
        maxNumOutstandingTxns=(
          // if this doesn't work, try increasing it.
          // It has been reduced to shrink the size of the counter for fmax
          // purposes
          4
        ),
      )
    )

    def mySlicedNonFbDcacheHost = myDcacheSlicer.io.devVec(
      (cfg.demoCfg.myFbCtrlMmapCfg.optAddrSliceVal.get + 1) % 2
    )
    def mySlicedFbDcacheHost = myDcacheSlicer.io.devVec(
      //1
      cfg.demoCfg.myFbCtrlMmapCfg.optAddrSliceVal.get
    )

    //def myFbDevBus = (
    //  myDcacheSlicer.io.devVec(
    //    cfg.demoCfg.myFbCtrlMmapCfg.optAddrSliceVal.get
    //  )
    //)

    def myFbArbiterHostIdxFbCtrl = 0
    def myFbArbiterHostIdxCpu = 1
    def limMyFbArbiterHostIdx = 2

    val myFbArbiter = LcvBusArbiter(
      cfg=LcvBusArbiterConfig(
        busCfg=(
          cfg.demoCfg.myFbCtrlMmapCfg.busCfg
        ),
        numHosts=limMyFbArbiterHostIdx,
        kind=LcvBusArbiterKind.Priority
      )
    )
    def myFbArbFbCtrlHost = myFbArbiter.io.hostVec(myFbArbiterHostIdxFbCtrl)
    def myFbArbCpuHost = myFbArbiter.io.hostVec(myFbArbiterHostIdxCpu)

    myFbArbFbCtrlHost <-/< myFbCtrl.io.bus

    val myFbCpuHostClone = cloneOf(myFbArbCpuHost)
    //myDcacheSlicer.io.devVec.last.h2dBus.translateInto(
    //  myFbCpuHostClone.h2dBus
    //)(
    //  dataAssignment=(outp, inp) => {
    //    outp.mainNonBurstInfo := inp.mainNonBurstInfo
    //    outp.mainBurstInfo := outp.mainBurstInfo.getZero
    //  }
    //)
    //mySlicedNonFbDcacheHost << cpu.io.lcvDbus
    myDcacheSlicer.io.host << cpu.io.lcvDbus

    myNonFbDcache.io.loBus << mySlicedNonFbDcacheHost

    mySlicedFbDcacheHost.h2dBus.translateInto(myFbCpuHostClone.h2dBus)(
      dataAssignment=(outp, inp) => {
        outp.mainNonBurstInfo := inp.mainNonBurstInfo
        outp.mainBurstInfo := outp.mainBurstInfo.getZero
      }
    )
    myFbCpuHostClone.d2hBus.translateInto(mySlicedFbDcacheHost.d2hBus)(
      dataAssignment=(outp, inp) => {
        outp.mainNonBurstInfo := inp.mainNonBurstInfo
      }
    )

    myFbArbCpuHost <-/< myFbCpuHostClone

    //myFbDcache.io.loBus <-/< myFbArbiter.io.dev
    icache.io.loBus <-/< cpu.io.lcvIbus
    mySdramCtrlIcacheHost <-/< icache.io.hiBus
    //myNonFbDcache.io.loBus << cpu.io.lcvDbus
    mySdramCtrlNonFbDcacheHost <-/< myNonFbDcache.io.hiBus

    val myFbDeburster = LcvBusDeburster(
      cfg=LcvBusDebursterConfig(
        loBusCfg=(
          //myFbDcache.cfg.loBusCfg
          myFbArbiter.cfg.busCfg
        )
      )
    )
    myFbDeburster.io.loBus <-/< myFbArbiter.io.dev
    myFbDcache.io.loBus <-/< myFbDeburster.io.hiBus
    //val myFbDCacheLoBusClone = cloneOf(myFbDcache.io.loBus)
    //myFbDcache.io.loBus

    mySdramCtrlFbDcacheHost <-/< myFbDcache.io.hiBus

    //myFbDbusSlicer
    //val myCpuCacheSdramCtrlBusArbiter
    //--------
    //--------
  }
}

case class MeltedMoonSimDutIo(
  cfg: MeltedMoonConfig,
) extends Bundle {
  //--------
	val vgaPhys = out(LcvVgaPhys(rgbConfig=cfg.demoCfg.rgbCfg))
	val vgaPixelEn = out(Bool())
	val vgaVisib = out(Bool())
	//val needResetMainLogic = out(Bool())
	//--------
}
case class MeltedMoonSimDut(
  cfg: MeltedMoonConfig
) extends Component {
  //--------
  val io = MeltedMoonSimDutIo(cfg=cfg)
  //--------
  //--------
  val meltedMoon = MeltedMoon(cfg=cfg)
  io.vgaPhys := meltedMoon.io.vgaPhys
  io.vgaPixelEn := meltedMoon.io.vgaPixelEn
  io.vgaVisib := meltedMoon.io.vgaVisib
  //--------
  val mySdram = as4c32m16sb()
  mySdram.io.DQ <> meltedMoon.io.sdram.dq
  mySdram.io.A := meltedMoon.io.sdram.a
  mySdram.io.DQML := meltedMoon.io.sdram.dqml
  mySdram.io.DQMH := meltedMoon.io.sdram.dqmh
  mySdram.io.BA := meltedMoon.io.sdram.ba
  mySdram.io.nCS := meltedMoon.io.sdram.nCs
  mySdram.io.nWE := meltedMoon.io.sdram.nWe
  mySdram.io.nRAS := meltedMoon.io.sdram.nRas
  mySdram.io.nCAS := meltedMoon.io.sdram.nCas
  mySdram.io.CLK := meltedMoon.io.sdram.clk
  mySdram.io.CKE := meltedMoon.io.sdram.cke
  //--------
  //val mainClkDomain = ClockDomain.current
  //mainClkDomain.reset.simPublic()
  val needResetMainLogic = Bool()
  //meltedMoon.clockDomain.readResetWire := needResetMainLogic
  //meltedMoon.mainClkDomain.reset := needResetMainLogic
  meltedMoon.io.mainLogicReset := needResetMainLogic
  val ioctlArea = new Area {
    val sdramInitRamInitBigInt = {
      val depth = 1 << (16 - 4)
      val tempArr = new ArrayBuffer[BigInt]()
      tempArr ++= cfg.demoCfg.program.outpArr.view
      //while (tempArr.size < depth) {
      //  tempArr += BigInt(0)
      //}
      val programSize = tempArr.size
      for (idx <- programSize until (1 << (16 - 4))) {
        //if (idx < /*1024*/0x800) {
        //  //println(
        //  //  s"idx < 0x800: ${idx}"
        //  //)
        //  tempArr += BigInt(idx)
        //} else {
        //  //println(
        //  //  s"idx < 0x800: ${idx}"
        //  //)
        //  tempArr += BigInt(0)
        //}
        tempArr += BigInt(0)
      }
      tempArr
      //for (elem <- program.outpArr.view) {
      //  tempArr +=
      //}
      //program.outpArr
    }
    case class MySdramModType(
    ) extends Bundle {
      val data = UInt(32 bits)
      val addr = UInt(32 bits)
    }
    val sdramInitRam = WrPulseRdPipeRamSdpPipe(
      cfg=WrPulseRdPipeRamSdpPipeConfig(
        modType=MySdramModType(),
        wordType=UInt(32 bits),
        wordCount=sdramInitRamInitBigInt.size,
        pipeName="sdramInitRam",
        setWordFunc=(
          (
            outp: MySdramModType,
            inp: MySdramModType,
            rdMemWord: UInt
          ) => {
            outp.data := rdMemWord
            outp.addr := inp.addr
          }
        ),
        initBigInt=Some(Array.fill(1)(sdramInitRamInitBigInt)),
      )
    )
    sdramInitRam.io.wrPulse := sdramInitRam.io.wrPulse.getZero
    val rRamRdAddrCnt = (
      Reg(UInt(log2Up(sdramInitRamInitBigInt.size + 1) + 1 bits))
      init(0x0)
    )
    when (sdramInitRam.io.rdAddrPipe.fire) {
      rRamRdAddrCnt := rRamRdAddrCnt + 1
    }
    sdramInitRam.io.rdAddrPipe.valid := (
      rRamRdAddrCnt(rRamRdAddrCnt.high downto 1)
      < sdramInitRamInitBigInt.size
    )
    sdramInitRam.io.rdAddrPipe.addr := (
      rRamRdAddrCnt(rRamRdAddrCnt.high downto 1).resize(
        sdramInitRam.io.rdAddrPipe.addr.getWidth
      )
    )
    sdramInitRam.io.rdAddrPipe.data.data := 0x0
    sdramInitRam.io.rdAddrPipe.data.addr := (
      //rRamRdAddrCnt(rRamRdAddrCnt.high downto 1).resize(
      //  sdramInitRam.io.rdAddrPipe.data.addr.getWidth
      //)
      Cat(
        rRamRdAddrCnt,
        U"1'b0"
      ).asUInt.resize(
        sdramInitRam.io.rdAddrPipe.data.addr.getWidth
      )
    )

    //mainClkDomain.softReset := (
    //  rRamRdAddrCnt < 0x100
    //)
    needResetMainLogic := (
      rRamRdAddrCnt < (0x800 >> 2)
    )

    meltedMoon.io.ioctl_download := (
      sdramInitRam.io.rdDataPipe.valid
    )
    meltedMoon.io.ioctl_wr := sdramInitRam.io.rdDataPipe.valid
    meltedMoon.io.ioctl_dout := meltedMoon.io.ioctl_dout.getZero
    switch (sdramInitRam.io.rdDataPipe.addr(1 downto 1)) {
      for (idx <- 0 until 2) {
        is (idx) {
          def outp = meltedMoon.io.ioctl_dout
          def inp = sdramInitRam.io.rdDataPipe
          //outp(
          //  (idx + 1) * inp.data.getWidth - 1
          //  downto idx * inp.data.getWidth
          //) := inp.data

          outp := inp.data(
            (idx + 1) * outp.getWidth - 1
            downto idx * outp.getWidth
          )
        }
      }
    }
    sdramInitRam.io.rdDataPipe.ready := !meltedMoon.io.ioctl_wait

    //meltedMoon.io.ioctl_dout := sdramInitRam.io.rdDataPipe.data

    meltedMoon.io.ioctl_addr := (
      sdramInitRam.io.rdDataPipe.addr.resize(
        meltedMoon.io.ioctl_addr.getWidth
      )
    )
  }
}
object MeltedMoonSimDutSim extends App {
  val cfg = MeltedMoonConfig(
    sdramCtrlUseAltddioOut=false
  )
  
  val numClkCycles = 8192 * 8 * 8 //* 8 * 8 * 8//2 //* 4//* 8 //* 4 * 8
  val myCfg = Config.spinalWithFreq(cfg.demoCfg.clkRate) 

  Config.simWithCfg(myCfg).compile({
    //val myClkDomain = ClockDomain.internal(
    //  name="core",
    //  config=myCfg.defaultConfigForClockDomains,
    //  withReset=true,
    //  withSoftReset=true,
    //)
    //val myClockingArea = new ClockingArea(myClkDomain) {
      val toComp = (
        //SnowHouseCpuFramebufferDemo(
        //  //program=testProgram.program,
        //  //doConnExternIrq=false,
        //  cfg=demoCfg,
        //)
        MeltedMoonSimDut(cfg=cfg)
      )
      //toComp.setDefinitionName(
      //  s"SnowHouseCpuWithSharedRam_${testIdx}_${instrRamKind}"
      //)
      //val temp = toComp.clockDomain
      //ClockDomain.current.reset.simPublic()
      //toComp.clockDomain.reset.simPublic()
      toComp
    //}
    //myClockingArea.toComp
  }).doSim{dut => {

    //println(
    //  s"help me out:${(1e9 ns) / demoCfg.clkRate}"
    //)
    //println(
    //  //s"${(((1 sec) / demoCfg.clkRate) * 1e9)} "
    //  //s"${(((1 sec) / demoCfg.clkRate)) ns}"
    //  s"${(((1 sec) / demoCfg.clkRate)) sec}"
    //)
    //dut.clockDomain.reset.simPublic()
    dut.clockDomain.forkStimulus(
      //(((1e9 ns) / demoCfg.clkRate) * 1.0).toInt //ns
      //8
      //((1e9) / demoCfg.clkRate)
      (((1 sec) / cfg.demoCfg.clkRate)) sec //ns //ms
    )
    //dut.meltedMoon.ioctlClkDomain.forkStimulus(
    //  (((1 sec) / cfg.demoCfg.clkRate)) sec //ns //ms
    //)
    dut.meltedMoon.vgaClkDomain.forkStimulus(
      //40
      //(((1 sec) / cfg.demoCfg.vgaTimingInfo.pixelClk)) sec //ns //ms
      (((1 sec) / cfg.demoCfg.clkRate)) sec //ns //ms
    )
    for (i <- 0 until numClkCycles) {
      dut.clockDomain.waitSampling()
      //dut.meltedMoon.ioctlClkDomain.waitSampling()
      dut.meltedMoon.vgaClkDomain.waitSampling()
      //dut.clockDomain.readResetWire #= dut.io.needResetMainLogic.toBoolean
      //var tickVgaClk: Boolean = false
      //if (
      //  (
      //    i
      //    % (
      //      //demoCfg.vgaTimingInfo.pixelClk / (1.0 MHz)
      //      demoCfg.clkRate / demoCfg.vgaTimingInfo.pixelClk /// (1.0 MHz)
      //    )
      //  ) == 0
      //) {
      //  tickVgaClk = true
      //  dut.vgaClkDomain.waitSampling()
      //}
      //println(
      //  s"i:${i}, tickVgaClk:${tickVgaClk}"
      //)
    }
  }}
}

object MeltedMoonToVerilog extends App {
  val cfg = MeltedMoonConfig(
    sdramCtrlUseAltddioOut=(
      true
    ),
  )
  Config.spinalWithFreq(clkRate=cfg.clkRate).generateVerilog{
    MeltedMoon(cfg=cfg)
    //LcvBusNonCoherentDataCacheWithSdramCtrl(
    //  sdramCtrlCfg=LcvBusSdramCtrlConfig(
    //    clkRate=cfg.clkRate,
    //  )
    //)
    //LcvSdramCtrlSimDut(
    //  clkRate=cfg.clkRate,
    //  useAltddioOut=true,
    //)
    //SnowHouseCpuWithDualRam(
    //  //program=cfg.testProgram.program
    //  //programStr="test/snowhousecpu-test-0.bin"
    //  SnowHouseCpuProgram(cfg=cfg.cpuCfg)
    //)
    //val temp = MeltedMoon(cfg=MeltedMoonConfig())
    //temp
  }
}

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
  val clkRate = (
    100.0 MHz
    //150.0 MHz 
  )
  val cpuCfg = SnowHouseCpuConfig(
    optFormal=false,
    targetAltera=true,
    //exposeModMemWordToIo=(
    //  //false
    //  true
    //),
    optMainAddrWidth=(
      Some(
        25
        //26
      )
    ),
    instrRamKind=0,
    programStr=(
      //"test/snowhousecpu-test-5.bin"
      //"test/snowhousecpu-framebuffer-demo.bin"
      "snowhousecpu-framebuffer-demo-320x240.bin"
    ),
    //exposeRegFileWriteDataToIo=true,
    optTwoCycleRegFileReads=(
      //true
      false
    ),
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
      "no_rw_check, MLAB"
      //"no_rw_check, logic"
      //"no_rw_check, M10K"
      //"no_rw_check, MLAB"
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
    rgbCfg=(
      //RgbConfig(rWidth=8, gWidth=8, bWidth=8)
      RgbConfig(rWidth=5, gWidth=5, bWidth=5)
    ),
    vgaTimingInfo=(
      LcvVgaTimingInfoMap.map("640x480@60")
      //LcvVgaTimingInfoMap.map("320x240@60")
      //LcvVgaTimingInfo(
      //  //pixelClk=12.5 MHz,
      //  //pixelClk=25.175 MHz,
      //  pixelClk=(
      //    //6.0 MHz
      //    25.0 MHz
      //  ),
      //  htiming=LcvVgaTimingHv(
      //    visib=320,
      //    front=8,
      //    sync=32,
      //    back=40,
      //  ),
      //  vtiming=LcvVgaTimingHv(
      //    visib=240,
      //    front=3,
      //    sync=4,
      //    back=6,
      //  ),
      //)
    ),
    fbCnt2dShift=ElabVec2[Int](
      x=(
        1
        //0
      ),
      y=1,
    ),
    fbAddrSliceHi=23,
    fbAddrSliceLo=23,
  )
  val sdramCtrlCfg = LcvBusSdramCtrlConfig(
    clkRate=clkRate,
    shortDqmToA12A11=sdramCtrlUseAltddioOut,
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
  //val mainLogicReset = in(Bool())
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
	val vgaPhys = out(
	  LcvVgaPhys(
      rgbConfig=(
        //cfg.demoCfg.rgbCfg
        RgbConfig(
          rWidth=8, gWidth=8, bWidth=8,
        )
      )
    )
  )
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

  val vgaClkDomain = ClockDomain.current
  //val vgaClkDomain = ClockDomain.external(
  //  name="vgaClk",
  //  withReset=true,//false,
  //  frequency=FixedFrequency(
  //    //25.0 MHz
  //    //cfg.demoCfg.vgaTimingInfo.pixelClk
  //    cfg.demoCfg.clkRate
  //  ),
  //)
  //val pixelFifo = StreamFifo/*CC*/(
  //  dataType=Rgb(cfg.demoCfg.rgbCfg),
  //  depth=(
  //    //io.misc.fifoDepth
  //    16
  //  ),
  //  latency=2,
  //  forFMax=true,
  //  //pushClock=(
  //  //  ClockDomain.current
  //  //  //mainClkDomain
  //  //),
  //  //popClock=vgaClkDomain,
  //)

  val myPixelPushStm = (
    Vec.fill(2)(
      //cloneOf(pixelFifo.io.push)
      Stream(Rgb(cfg.demoCfg.rgbCfg))
    )
  )
  val myPixelMuxSel = UInt(1 bits)
  val myPixelMuxStm = (
    StreamMux(
      select=myPixelMuxSel,
      inputs=myPixelPushStm,
    )
  )
  val vblankIrqFifo = StreamFifo/*CC*/(
    dataType=Bool(),
    depth=(
      4
    ),
    latency=2,
    forFMax=true,
    //pushClock=vgaClkDomain,
    //popClock=(
    //  ClockDomain.current
    //  //mainClkDomain
    //),
  )
  def cpp = LcvVgaCtrl.cpp(
    clkRate=cfg.clkRate,
    vgaTimingInfo=cfg.demoCfg.vgaTimingInfo,
  )
  println(
    s"here we go: cpp:${cpp}"
  )
  //val vgaClockingArea = new ClockingArea(vgaClkDomain) 
  val vgaArea = new Area {
    //val vgaCtrl = VgaCtrl(rgbConfig=cfg.demoCfg.rgbCfg)

    val vgaTimingInfo = cfg.demoCfg.vgaTimingInfo
    ////if (vgaTimingInfo == LcvVgaTimingInfoMap.map("640x480@60")) {
    ////  vgaCtrl.io.timings.setAs_h640_v480_r60
    ////} else if (vgaTimingInfo == LcvVgaTimingInfoMap.map("1920x1080@60")) {
    ////  vgaCtrl.io.timings.setAs_h1920_v1080_r60
    ////} else {
    //  // TODO: check if this works?
    //  vgaTimingInfo.driveSpinalVgaTimings(
    //    clkRate=(
    //      cfg.clkRate
    //    ),
    //    spinalVgaTimings=vgaCtrl.io.timings,
    //  )
    ////}

    val lcvVgaCtrl = (
      LcvVgaCtrl(
        clkRate=cfg.clkRate,
        //rgbConfig=physRgbConfig,
        rgbConfig=cfg.demoCfg.rgbCfg,
        vgaTimingInfo=cfg.demoCfg.vgaTimingInfo,
        fifoDepth=(
          //cfg.ctrlFifoDepth
          //io.misc.fifoDepth
          32
        ),
      )
    )
    //io.vgaPhys := (
    //  RegNext(
    //    lcvVgaCtrl.io.phys,
    //    init=lcvVgaCtrl.io.phys.getZero,
    //  )
    //)
    //io.misc := lcvVgaCtrl.io.misc
    //io.vgaVisib := (
    //  RegNext(
    //    lcvVgaCtrl.io.misc.visib,
    //    init=lcvVgaCtrl.io.misc.visib.getZero,
    //  )
    //)
    //io.vgaPixelEn := (
    //  RegNext(
    //    lcvVgaCtrl.io.misc.pixelEn,
    //    init=lcvVgaCtrl.io.misc.pixelEn.getZero,
    //  )
    //)
    io.vgaPhys.setAsReg() init(io.vgaPhys.getZero)
    io.vgaVisib.setAsReg() init(io.vgaVisib.getZero)
    io.vgaPixelEn.setAsReg() init(io.vgaPixelEn.getZero)

    //io.vgaPhys := lcvVgaCtrl.io.phys
    io.vgaPhys.hsync := lcvVgaCtrl.io.phys.hsync
    io.vgaPhys.vsync := lcvVgaCtrl.io.phys.vsync
    io.vgaVisib := lcvVgaCtrl.io.misc.visib
    io.vgaPixelEn := lcvVgaCtrl.io.misc.pixelEn //RegNext(lcvVgaCtrl.io.misc.pixelEn, init=False)

    val mySeenPixelPushStmValid = Bool()
    val rSavedSeenPixelPushStmValid = Reg(Bool(), init=False)
    val stickySeenMyPixelPushStmValid = (
      mySeenPixelPushStmValid
      || rSavedSeenPixelPushStmValid
    )
    mySeenPixelPushStmValid := myPixelPushStm.last.valid
    when (mySeenPixelPushStmValid) {
      rSavedSeenPixelPushStmValid := True
    }

    lcvVgaCtrl.io.en := True//stickySeenMyPixelPushStmValid
    lcvVgaCtrl.io.push << myPixelMuxStm
    when (lcvVgaCtrl.io.misc.visib) {
      io.vgaPhys.col.r(7 downto 3) := lcvVgaCtrl.io.phys.col.r
      io.vgaPhys.col.g(7 downto 3) := lcvVgaCtrl.io.phys.col.g
      io.vgaPhys.col.b(7 downto 3) := lcvVgaCtrl.io.phys.col.b
    } otherwise {
      io.vgaPhys.col := io.vgaPhys.col.getZero
    }

    //--------
    //when (vgaCtrl.io.vga.colorEn) {
    //  io.vgaPhys.col := vgaCtrl.io.vga.color
    //} otherwise {
    //  io.vgaPhys.col := io.vgaPhys.col.getZero
    //}
    //io.vgaPhys.hsync := vgaCtrl.io.vga.hSync
    //io.vgaPhys.vsync := vgaCtrl.io.vga.vSync
    ////io.misc := io.misc.getZero
    ////io.misc.allowOverride
    ////io.misc.pastVisib := RegNext(io.misc.visib) init(False)
    //io.vgaVisib := vgaCtrl.io.vga.colorEn
    ////io.misc.pixelEn := (
    ////  True
    ////)
    //vgaCtrl.io.softReset := RegNext(False) init(True)
    ////vgaCtrl.io.pixels <-/< myFbCtrl.io.pop
    ////vgaCtrl.io.pixels << pixelFifo.io.pop.repeat(
    ////  times=cpp
    ////)._1
    //vgaCtrl.io.pixels << myPixelPushStm.repeat(
    //  times=cpp
    //)._1

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
      kind=(
        //LcvBusArbiterKind.Priority
        LcvBusArbiterKind.RoundRobin
      ),
    )
  )
  //mySdramCtrl.io.bus <-/< mySdramCtrlBusArbiter.io.dev
  val mySdramDeburster = LcvBusDeburster(
    cfg=LcvBusDebursterConfig(
      loBusCfg=(
        cfg.sdramCtrlCfg.busCfg
        //LcvBusConfig(
        //  mainCfg=cfg.sdramCtrlCfg.busCfg.mainCfg.mkCopyWithoutAllowingBurst()
        //)
      )
    )
  )
  mySdramDeburster.io.loBus <-/< mySdramCtrlBusArbiter.io.dev
  val myTempSdramDebursterHiBus = cloneOf(mySdramDeburster.io.hiBus)
  myTempSdramDebursterHiBus <-/< mySdramDeburster.io.hiBus
  myTempSdramDebursterHiBus.h2dBus.translateInto(
    mySdramCtrl.io.bus.h2dBus
  )(
    dataAssignment=(outp, inp) => {
      outp.mainNonBurstInfo := inp.mainNonBurstInfo
      outp.mainBurstInfo := outp.mainBurstInfo.getZero
    }
  )
  mySdramCtrl.io.bus.d2hBus.translateInto(
    myTempSdramDebursterHiBus.d2hBus
  )(
    dataAssignment=(outp, inp) => {
      outp.mainNonBurstInfo := inp.mainNonBurstInfo
    }
  )

  //mySdramDeburster.io.hiBus.translateInto(
  //)

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
      val isWrite = Bool()
      val byteEn = UInt(cfg.sdramCtrlCfg.busCfg.byteEnWidth bits)
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
    //val rIoctlPushCnt = (
    //  Reg(UInt(
    //    //1 bits
    //    log2Up(
    //      cfg.sdramCtrlCfg.busCfg.dataWidth
    //      / cfg.ioctlSpinalDw
    //    ).toInt
    //    bits
    //  )) init(0x0)
    //)
    val myIoctlPushCntWidth = (
      log2Up(
        cfg.sdramCtrlCfg.busCfg.dataWidth
        / cfg.ioctlSpinalDw
      ).toInt
    )
    val rIoctlRecvPushLaggingAddrCond = (
      RegNextWhen(
        True,
        cond=(
          myIoctlRecvPushStm.head.addr >= (4 * 4 * 4 * 2)
        ),
        init=False
      )
    )
    myIoctlRecvPushStm.head.translateInto(myIoctlRecvPushStm.last)(
      dataAssignment=(outp, inp) => {
        //outp.addr := inp.addr
        outp.addr := (
          RegNext(
            outp.addr,
            init=outp.addr.getZero
          )
        )
        //outp.isWrite := inp.isWrite
        outp.data := (
          //outp.data.getZero
          RegNext(
            outp.data,
            init=outp.data.getZero,
          )
        )
        outp.byteEn := 0x0
        switch (
          inp.addr(myIoctlPushCntWidth + 1 - 1 downto 1)
        ) {
          for (idx <- 0 until (1 << myIoctlPushCntWidth)) {
            is (idx) {
              //outp.byteEn := (
              //  0x3 << (idx * 2)
              //)
              println(
                (idx + 1) * inp.data.getWidth - 1
                downto idx * inp.data.getWidth
              )
              outp.data(
                (idx + 1) * inp.data.getWidth - 1
                downto idx * inp.data.getWidth
              ) := inp.data
              if (idx == 0) {
                when (rIoctlRecvPushLaggingAddrCond) {
                  outp.addr := inp.addr - (4 * 4 * 4 * 2)
                } otherwise {
                  outp.addr := outp.addr.getZero
                }
                outp.isWrite := False
                outp.byteEn := 0x0
              } else {
                outp.addr := inp.addr
                outp.isWrite := inp.isWrite
                outp.byteEn := (
                  U(outp.byteEn.getWidth bits, default -> True)
                )
              }
            }
          }
        }
      }
    )
    myIoctlRecvFifo.io.push <-/< myIoctlRecvPushStm.last
    //when (
    //  myIoctlRecvPushStm.last.fire
    //) {
    //  rIoctlPushCnt := rIoctlPushCnt + 1
    //}
    //val codeIndex = io.ioctl_index.orR//andR
    //val codeDownload = io.ioctl_download && codeIndex
    val cartDownload = (
      io.ioctl_wr
      && io.ioctl_download
      && io.ioctl_index(5 downto 0) === 0x1
      //&& codeIndex
      //&& !codeIndex
      //&& (io.ioctl_index =/= 4)
      //&& (io.ioctl_index =/= 254)
    )
    val myIoctlRecvPushValidCond = (
      //io.ioctl_wr && io.ioctl_download && cartDownload
      cartDownload
    )
    myPixelMuxSel.lsb := vgaArea.stickySeenMyPixelPushStmValid
    myIoctlRecvPushStm.head.valid := (
      //io.ioctl_upload 
      //RegNextWhen(
      //  False,
      //  cond=(
      //    //io.ioctl_download
      //    myIoctlRecvPushValidCond
      //  ),
      //  init=True
      //)
      ////|| (
      ////  myIoctlRecvPushValidCond
      ////)
      //|| 
      (
        //io.ioctl_download
        //&& io.ioctl_wr
        cartDownload
      )
    )
    myIoctlRecvPushStm.head.data := io.ioctl_dout
    myIoctlRecvPushStm.head.addr := (
      io.ioctl_addr.resize(myIoctlRecvPushStm.head.addr.getWidth)
    )
    myIoctlRecvPushStm.head.isWrite := (
      cartDownload
      || RegNextWhen(
        True,
        cond=cartDownload,
        init=False,
      )
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
        outp.isWrite := (
          //True
          inp.isWrite
          //RegNextWhen(
          //  True,
          //  cond=myIoctlRecvPushValidCond,
          //  init=False
          //)
        )
        outp.byteEn := inp.byteEn
        //when (
        //  inp.addr(2)
        //  === RegNextWhen(
        //    myIoctlRecvPopStm.addr(2),
        //    cond=myIoctlRecvPopStm.fire,
        //    init=myIoctlRecvPopStm.addr(2).getZero
        //  )
        //  //|| !History[Bool](
        //  //  that=True,
        //  //  when=myIoctlRecvPopStm.fire,
        //  //  length=2,
        //  //  init=False,
        //  //).last
        //) {
        //  outp.byteEn := 0xc
        //} otherwise {
        //  outp.byteEn := 0x3
        //}
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

  //val rPixelEnCnt = Reg(UInt(
  //  log2Up(cpp) bits
  //))
  //io.vgaPixelEn := (
  //  rPixelEnCnt === cpp - 1
  //)
  ////when (
  ////  vgaClockingArea.vgaCtrl.io.pixels.fire
  ////  || !vgaClockingArea.vgaCtrl.io.pixels.ready
  ////) {
  //  when (rPixelEnCnt < cpp - 1) {
  //    rPixelEnCnt := rPixelEnCnt + 1
  //  } otherwise {
 //    rPixelEnCnt := 0x0
  //  }
  ////}
  val myMainResetCond = (
    //io.mainLogicReset,
    //RegNextWhen(
    //  False,
    //  cond=(
        RegNextWhen(
          False,
          cond=(
            //io.ioctl_download
            ioctlArea.cartDownload
          ),
          init=True,
        )
        || io.ioctl_download
        || sdramInitFifo.io.pop.valid
    //  ),
    //  init=True
    //)
  )
  //mySdramCtrlBusArbiter.io.en := (
  //  //True
  //  //!myMainResetCond
  //  RegNextWhen(
  //    True,
  //    cond=(!myMainResetCond),
  //    init=False
  //  )
  //)

  val main = new ResetArea(
    myMainResetCond,
    //false
    true
  ) {
    val myFbCtrl = LcvBusFramebufferCtrl(
      cfg=(
        cfg.demoCfg.myFbCfg
        //myDbgFbCfg
      )
    )
    //myFbCtrl.io.pop.ready := True
    //pixelFifo.io.push <-/< myFbCtrl.io.pop
    myPixelPushStm.last <-/< myFbCtrl.io.pop
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
    //val dcacheArr = Array.fill(limMyDcacheArrIdx)(
    //  LcvBusCache(cfg=cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg)
    //)
    val dcacheArr = Array(
      LcvBusCache(cfg=LcvBusCacheBusPairConfig(
        mainCfg=cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg.mainCfg,
        loBusCacheCfg=LcvBusCacheConfig(
          kind=LcvCacheKind.D,
          lineSizeBytes=64,
          depthWords=(
            //4 * 1024 / (4 * 2)
            //256
            //64
            128
          ).toInt,
          numCpus=1,
          lineWordMemRamStyleAltera=(
            "no_rw_check, M10K"
          ),
          lineAttrsMemRamStyleAltera=(
            "no_rw_check, MLAB"
          ),
        ),
        hiBusCacheCfg=None,
      )),
      LcvBusCache(cfg=cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg),
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
        kind=(
          //LcvBusArbiterKind.Priority
          LcvBusArbiterKind.RoundRobin
        )
      )
    )
    //myFbArbiter.io.en := True
    def myFbArbFbCtrlHost = (
      myFbArbiter.io.hostVec(myFbArbiterHostIdxFbCtrl)
    )
    def myFbArbCpuHost = (
      myFbArbiter.io.hostVec(myFbArbiterHostIdxCpu)
    )

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

    //icache.io.loBus <-/< cpu.io.lcvIbus
    val myTempCpuLcvIbusD2hStm = cloneOf(cpu.io.lcvIbus.d2hBus)
    val myHistCpuIbusD2hFire = History[Bool](
      that=True,
      length=2,
      when=cpu.io.lcvIbus.d2hBus.fire,
      init=False,
    )
    icache.io.loBus.h2dBus <-/< cpu.io.lcvIbus.h2dBus
    icache.io.loBus.d2hBus.translateInto(myTempCpuLcvIbusD2hStm)(
      dataAssignment=(outp, inp) => {
        outp := inp
        val tempCond = (
          inp.src
          === (
            RegNextWhen(
              inp.src,
              cond=icache.io.loBus.d2hBus.fire,
              //init=(
              //  //inp.src.getZero
              //  0x2
              //),
            )
            init(0x2)
          )
        )
        val rState = Reg(Bool(), init=False)
        when (
          !myHistCpuIbusD2hFire.last
          || (
            tempCond
            && !rState
            //&& RegNextWhen(
            //  False,
            //  cond=(
            //    //(!tempCond)
            //    //&& icache.io.loBus.d2hBus.fire
            //    inp.src
            //    === RegNextWhen(
            //      inp.src + 1,
            //      cond=icache.io.loBus.d2hBus.fire,
            //      init=inp.src.getZero,
            //    )
            //  ),
            //  init=True,
            //)
            //&& RegNextWhen(
            //  False,
            //  cond=tempCond,
            //  init=True,
            //)
          )
        ) {
          outp.data := outp.data.getZero
          when (
            inp.src === 0x3
          ) {
            rState := True
          }
        }
      }
    )
    cpu.io.lcvIbus.d2hBus <-/< myTempCpuLcvIbusD2hStm

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
    val myDbgBadData = Array[Long](
      //0x00100008,
      //0x00080010,
      //0x0010,
      //0x0008,
      0x0028002a
    )
    val myDbgAddrArr = Array[Long](
      //0x6c8,
      //0x6ca,
      0x00800bc0
    )
    val myDbgFbDcacheCondVec = (
      KeepAttribute(
        Vec[Bool](List[Bool](
          (
            myFbDcache.io.loBus.h2dBus.addr === myDbgAddrArr(0x0)
            && myFbDcache.io.loBus.h2dBus.isWrite
          ),
          (
            myFbDcache.io.loBus.h2dBus.addr === myDbgAddrArr(0x0)
            && myFbDcache.io.loBus.h2dBus.data === myDbgBadData(0x0)
            && myFbDcache.io.loBus.h2dBus.isWrite
          ),
          (
            myFbDcache.io.hiBus.h2dBus.addr === myDbgAddrArr(0x0)
            && myFbDcache.io.hiBus.h2dBus.isWrite
          )
        ))
      )
    )
    //val myDbgBadData = Array[Int](
    //  //0x00100008,
    //  //0x00080010,
    //  0x0010,
    //  0x0008,
    //)
    //val myDbgAddrArr = Array[Int](
    //  0x6c8,
    //  0x6ca,
    //)
    //val myDbgSdramWrDataCondVec = (
    //  KeepAttribute(
    //    Vec[Bool](List[Bool](
    //      (
    //        //mySdramCtrl.io.sdram.dq(11 downto 0)  === 0x15b
    //        mySdramCtrl.io.sdram.dq(15 downto 0)  === 0x0010
    //        || RegNext(
    //          (mySdramCtrl.io.sdram.dq(15 downto 0)  === 0x0008),
    //          init=False
    //        )
    //      ),
    //      (
    //        //mySdramCtrl.io.sdram.dq(15 downto 0)  === 0x0010
    //        //&& RegNext(
    //        //  (mySdramCtrl.io.sdram.dq(15 downto 0)  === 0x0008),
    //        //  init=False
    //        //)
    //        //cpu.io.lcvDbus.h2dBus.data === 0x00100008
    //        //&& cpu.io.lcvDbus.h2dBus.valid
    //        //myFbArbiter.io.dev.h2dBus.data === 0x00100008//0x00080010
    //        //&& myFbArbiter.io.dev.h2dBus.valid
    //        (
    //          (
    //            myFbArbiter.io.dev.h2dBus.data(15 downto 0)
    //              === myDbgBadData(0)//0x00080010
    //          )
    //          || (
    //            myFbArbiter.io.dev.h2dBus.data(15 downto 0)
    //            === myDbgBadData(1)
    //          )
    //        ) && (
    //          (
    //            myFbArbiter.io.dev.h2dBus.addr(15 downto 0)
    //            === myDbgAddrArr(0)
    //          )
    //          || (
    //            myFbArbiter.io.dev.h2dBus.addr(15 downto 0)
    //            === myDbgAddrArr(1)
    //          )
    //        )
    //      ),
    //      (
    //        (
    //          (
    //            myFbDcache.io.loBus.h2dBus.data(15 downto 0)
    //            === myDbgBadData(0)
    //          )
    //          || (
    //            myFbDcache.io.loBus.h2dBus.data(15 downto 0)
    //            === myDbgBadData(1)
    //          )
    //        ) && (
    //          (
    //            myFbDcache.io.loBus.h2dBus.addr(15 downto 0)
    //            === myDbgAddrArr(0)
    //          )
    //          || (
    //            myFbDcache.io.loBus.h2dBus.addr(15 downto 0)
    //            === myDbgAddrArr(1)
    //          )
    //        )
    //      ),
    //      (
    //        (
    //          (
    //            myFbDeburster.io.loBus.h2dBus.data(15 downto 0)
    //            === myDbgBadData(0)
    //          )
    //          || (
    //            myFbDeburster.io.loBus.h2dBus.data(15 downto 0)
    //            === myDbgBadData(1)
    //          )
    //          //&& myFbArbiter.io.dev.h2dBus.valid
    //        ) && (
    //          (
    //            myFbDeburster.io.loBus.h2dBus.addr(15 downto 0)
    //            === myDbgAddrArr(0)
    //          )
    //          || (
    //            myFbDeburster.io.loBus.h2dBus.addr(15 downto 0)
    //            === myDbgAddrArr(1)
    //          )
    //        )
    //      )
    //    ))
    //  )
    //)

  }
  //when (io.mainLogicReset) {
    //pixelFifo.io.push <-/< myPixelPushStm
    //vgaClockingArea.lcvVgaCtrl.io.push << myPixelPushStm
    myPixelPushStm.head.valid := True //io.mainLogicReset //True io.mainLogicReset
    myPixelPushStm.head.r := (
      RegNextWhen(
        (myPixelPushStm.head.r + 1) & 0x1f,//0xff,
        cond=(
          //RegNext(myPixelPushStm.head.fire, init=False)
          myPixelPushStm.head.fire
          && io.vgaPixelEn
        ),
        init=myPixelPushStm.head.r.getZero,
      )
      //U(myPixelPushStm.head.r.getWidth bits, default -> True)
    )
    myPixelPushStm.head.g := (
      RegNextWhen(
        (myPixelPushStm.head.g + 1) & 0x1f,//0xff,
        cond=(
          //RegNext(myPixelPushStm.head.fire, init=False)
          myPixelPushStm.head.fire
          && io.vgaPixelEn
        ),
        init=myPixelPushStm.head.g.getZero,
      )
      //U(myPixelPushStm.head.g.getWidth bits, default -> True)
    )
    myPixelPushStm.head.b := (
      RegNextWhen(
        (
          (myPixelPushStm.head.b + 1) & 0x1f,//0xff,
        ),
        cond=(
          //RegNext(myPixelPushStm.head.fire, init=False)
          myPixelPushStm.head.fire
          && io.vgaPixelEn
        ),
        init=myPixelPushStm.head.b.getZero,
      )
      //U(myPixelPushStm.head.b.getWidth bits, default -> True)
    )
    //myPixelPushStm.g := (
    //  U(myPixelPushStm.g.getWidth bits, default -> True)
    //)
    //myPixelPushStm.b := (
    //  U(myPixelPushStm.b.getWidth bits, default -> True)
    //)
    //myPixelPushStm.ready := False
  //}
}

case class MeltedMoonSimDutIo(
  cfg: MeltedMoonConfig,
) extends Bundle {
  //--------
	val vgaPhys = out(LcvVgaPhys(
	  rgbConfig=(
	    //cfg.demoCfg.rgbCfg
	    RgbConfig(rWidth=8, gWidth=8, bWidth=8)
	  )
	))
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
  //meltedMoon.io.mainLogicReset := needResetMainLogic
  val ioctlArea = new Area {
    val sdramInitRamInitBigInt = {
      val depth = 1 << (16 - 4)
      val tempArr = new ArrayBuffer[BigInt]()
      tempArr ++= cfg.demoCfg.program.outpArr.view
      //while (tempArr.size < depth) {
      //  tempArr += BigInt(0)
      //}
      //val programSize = tempArr.size
      //for (idx <- programSize until (1 << (16 - 4))) {
      //  if (idx < /*1024*/0x800) {
      //    //println(
      //    //  s"idx < 0x800: ${idx}"
      //    //)
      //    //tempArr += BigInt(idx)
      //    tempArr += BigInt(0)
      //  } else {
      //    //println(
      //    //  s"idx < 0x800: ${idx}"
      //    //)
      //    //tempArr += BigInt(0)
      //  }
      //  //tempArr += BigInt(0)
      //}
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

    meltedMoon.io.ioctl_index := (
      meltedMoon.io.ioctl_index.getZero
      //Cat(
      //  sdramInitRam.io.rdDataPipe.valid
      //).asUInt.resize(meltedMoon.io.ioctl_index.getWidth)
    )
    meltedMoon.io.ioctl_index.allowOverride
    meltedMoon.io.ioctl_index.lsb := (
      sdramInitRam.io.rdDataPipe.valid
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
  
  val numClkCycles = 8192 * 8 * 8 * 8 * 8 // * 8//2 //* 4//* 8 //* 4 * 8
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
    //dut.meltedMoon.vgaClkDomain.forkStimulus(
    //  //40
    //  //(((1 sec) / cfg.demoCfg.vgaTimingInfo.pixelClk)) sec //ns //ms
    //  (((1 sec) / cfg.demoCfg.clkRate)) sec //ns //ms
    //)
    for (i <- 0 until numClkCycles) {
      dut.clockDomain.waitSampling()
      //dut.meltedMoon.ioctlClkDomain.waitSampling()
      //dut.meltedMoon.vgaClkDomain.waitSampling()
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

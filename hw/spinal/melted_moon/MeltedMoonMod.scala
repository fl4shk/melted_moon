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
import scala.math._

import java.io._

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
  dbgExposeExtrasAtRegFileWrite: Boolean=false,
  dbgUseLcvBusMem: Boolean=false,
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
        //25
        //26
        //27
        28
      )
    ),
    instrRamKind=0,
    programStr=(
      //"test/snowhousecpu-test-5.bin"
      //"test/snowhousecpu-framebuffer-demo.bin"
      //"other_tests.ignore/melted_moon_doom.bin"
      //"other_tests.ignore/melted_moon_doom-enable_irqs.bin"
      "other_tests.ignore/my_software_3d_renderer.bin"
      //"debug/snowhousecpu-framebuffer-demo-320x240.bin"
    ),
    exposeRegFileWriteDataToIo=dbgExposeExtrasAtRegFileWrite,
    exposeRegFileWriteAddrToIo=dbgExposeExtrasAtRegFileWrite,
    exposeRegFileWriteEnableToIo=dbgExposeExtrasAtRegFileWrite,
    dbgExposeExtrasAtRegFileWrite=dbgExposeExtrasAtRegFileWrite,
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
      //"no_rw_check, MLAB"
      "no_rw_check, M10K"
      //"no_rw_check, logic"
      //"no_rw_check, MLAB"
    ),
    dcacheLineWordMemRamStyleAltera=(
      //"no_rw_check, MLAB"
      "no_rw_check, M10K"
      //"MLAB"
    ),
    dcacheLineAttrsMemRamStyleAltera=(
      //"no_rw_check, MLAB"
      "no_rw_check, M10K"
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

  def program = testProgram.program
  val myDbusCfg = cpuCfg.shCfg.subCfg.lcvDbusEtcCfg.loBusCfg
  val rgbCfg=(
    //RgbConfig(rWidth=8, gWidth=8, bWidth=8)
    RgbConfig(rWidth=5, gWidth=5, bWidth=5)
  )
  val vgaTimingInfo = (
    LcvVgaTimingInfoMap.map("640x480@70")
    //LcvVgaTimingInfoMap.map("640x480@60")
    //LcvVgaTimingInfoMap.map("320x240@60")
    //LcvVgaTimingInfo(
    //  //pixelClk=12.5 MHz,
    //  //pixelClk=25.175 MHz,
    //  pixelClk=(
    //    //6.0 MHz
    //    25.0 MHz
    //    //12.5 MHz
    //  ),
    //  htiming=LcvVgaTimingHv(
    //    visib=(
    //      320
    //      //160
    //    ),
    //    front=8,
    //    sync=32,
    //    back=40,
    //  ),
    //  vtiming=LcvVgaTimingHv(
    //    visib=(
    //      //240
    //      //120
    //      60
    //    ),
    //    front=3,
    //    sync=4,
    //    back=6,
    //  ),
    //)
  )
  val fbCnt2dShift = (
    ElabVec2[Int](
      x=(
        1
        //0
      ),
      y=1,
    )
  )
  val myDbusSlicerAddrSliceHi = 26//27
  val myDbusSlicerAddrSliceLo = 25//26

  def myNonFbSdramAddrSliceVal = 0x0
  //val myFbOptAddrSliceVal = Some(1)
  def myFbAddrSliceVal = 0x1
  def myIoIrqCtrlAddrSliceVal = 0x2
  def myIoDbgPrintAddrSliceVal = 0x3

  val myDbusSlicerMmapCfg = LcvBusMemMapConfig(
    busCfg=(
      myDbusCfg
      //LcvBusConfig(
      //  mainCfg=myDbusCfg.mainCfg.mkCopyWithAllowingBurst(),
      //  cacheCfg=myDbusCfg.cacheCfg,
      //)
    ),
    addrSliceHi=myDbusSlicerAddrSliceHi,//25,
    addrSliceLo=myDbusSlicerAddrSliceLo,//25,
    optAddrSliceVal=(
      // the framebuffer has bit 25 of the address asserted!
      //Some(1)
      //myFbOptAddrSliceVal
      None
    )
  )

  val fbAddrSliceHi = 25//26 // memory-mapped IO registers start at (1 << 27)
  val fbAddrSliceLo = 25//26

  val myFbCtrlMmapCfg = LcvBusMemMapConfig(
    busCfg=(
      //myDbusCfg
      LcvBusConfig(
        mainCfg=myDbusCfg.mainCfg.mkCopyWithAllowingBurst(),
        cacheCfg=myDbusCfg.cacheCfg,
      )
    ),
    addrSliceHi=fbAddrSliceHi,//24,
    addrSliceLo=fbAddrSliceLo,//24,
    optAddrSliceVal=(
      // the framebuffer has bit 24 of the address asserted!
      //Some(1)
      Some(myFbAddrSliceVal)
      //None
    )
  )
  val myFbCfg = LcvBusFramebufferConfig(
    fbMmapCfg=myFbCtrlMmapCfg,
    rgbCfg=rgbCfg,
    //vgaTimingInfo=(
    //  //LcvVgaTimingInfoMap.map("320x240@60")
    //  vgaTimingInfo
    //),
    fbSize2d=(
      //vgaTimingInfo.fbSize2d
      ElabVec2[Int](
        x=(
          vgaTimingInfo.fbSize2d.x
          //320
          //>> (if (fbCnt2dShift.x) (1) else (0))
          >> fbCnt2dShift.x
        ),
        y=(
          vgaTimingInfo.fbSize2d.y
          //240
          //>> (if (fbCnt2dShift.y) (1) else (0))
          >> fbCnt2dShift.y
        ),
      )
    ),
    cnt2dShift=fbCnt2dShift,
    dblBuf=(
      //true
      false
    ),
  )

  //val demoCfg = SnowHouseCpuFramebufferDemoConfig(
  //  program=testProgram.program,
  //  clkRate=clkRate,
  //  rgbCfg=(
  //    //RgbConfig(rWidth=8, gWidth=8, bWidth=8)
  //    RgbConfig(rWidth=5, gWidth=5, bWidth=5)
  //  ),
  //  vgaTimingInfo=(
  //    LcvVgaTimingInfoMap.map("640x480@70")
  //    //LcvVgaTimingInfoMap.map("640x480@60")
  //    //LcvVgaTimingInfoMap.map("320x240@60")
  //    //LcvVgaTimingInfo(
  //    //  //pixelClk=12.5 MHz,
  //    //  //pixelClk=25.175 MHz,
  //    //  pixelClk=(
  //    //    //6.0 MHz
  //    //    25.0 MHz
  //    //    //12.5 MHz
  //    //  ),
  //    //  htiming=LcvVgaTimingHv(
  //    //    visib=(
  //    //      320
  //    //      //160
  //    //    ),
  //    //    front=8,
  //    //    sync=32,
  //    //    back=40,
  //    //  ),
  //    //  vtiming=LcvVgaTimingHv(
  //    //    visib=(
  //    //      //240
  //    //      //120
  //    //      60
  //    //    ),
  //    //    front=3,
  //    //    sync=4,
  //    //    back=6,
  //    //  ),
  //    //)
  //  ),
  //  fbCnt2dShift=ElabVec2[Int](
  //    x=(
  //      1
  //      //0
  //    ),
  //    y=1,
  //  ),
  //  fbAddrSliceHi=27, // memory-mapped IO registers start at (1 << 27)
  //  fbAddrSliceLo=26,
  //)
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

case class MeltedMoonDbgPrintIo(
  cfg: MeltedMoonConfig,
) extends Bundle {
  val bus = slave(
    LcvBusIo(cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg.loBusCfg)
  )
  val outpChar = out(Flow(UInt(8 bits)))
}

case class MeltedMoonDbgPrint(
  cfg: MeltedMoonConfig,
) extends Component {
  //--------
  def busCfg = cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg.loBusCfg
  //--------
  val io = MeltedMoonDbgPrintIo(cfg=cfg)
  //--------
  io.bus.h2dBus.ready := False
  io.bus.d2hBus.valid := False
  io.bus.d2hBus.payload := io.bus.d2hBus.payload.getZero

  //io.outpChar.setAsReg() init(io.outpChar.getZero)
  io.outpChar := io.outpChar.getZero
  io.outpChar.simPublic()
  //--------
  object State
  extends SpinalEnum(defaultEncoding=binarySequential) {
    val
      IDLE,
      BUS_RD,
      BUS_WR
      = newElement();
  }
  val rState = (
    Reg(State())
    init(State.IDLE)
  )

  val rSavedH2dPayload = (
    Reg(
      cloneOf(io.bus.h2dBus.payload),
      init=io.bus.h2dBus.payload.getZero
    )
  )

  switch (rState) {
    is (State.IDLE) {
      rSavedH2dPayload := io.bus.h2dBus.payload

      switch (
        io.bus.h2dBus.valid
        ## io.bus.h2dBus.isWrite
      ) {
        is (B"10") {
          io.bus.h2dBus.ready := True
          rState := State.BUS_RD
        }
        is (B"11") {
          io.bus.h2dBus.ready := True
          rState := State.BUS_WR
        }
        default {
        }
      }
    }
    is (State.BUS_RD) {
      io.bus.d2hBus.valid := True
      io.bus.d2hBus.src := rSavedH2dPayload.src
      when (io.bus.d2hBus.fire) {
        rState := State.IDLE
      }
    }
    is (State.BUS_WR) {
      //switch (rSavedH2dPayload.data(7 downto 0)) {
      //  for (idx <- '\n'.toInt + 1 until 'z'.toInt) {
      //    is (idx) {
      //      //var toPrint: String = ""
      //      //toPrint = toPrint + idx.toChar
      //      report(
      //        //Seq(rSavedH2dPayload.data(7 downto 0).toString())
      //        //Seq(idx.toChar().toString())
      //        //toPrint
      //        "" + idx.toChar
      //      )
      //    }
      //  }
      //  is ('\n'.toInt) {
      //  }
      //}
      io.outpChar.valid := io.bus.d2hBus.fire
      io.outpChar.payload := rSavedH2dPayload.data(7 downto 0)

      io.bus.d2hBus.valid := True
      io.bus.d2hBus.src := rSavedH2dPayload.src
      when (io.bus.d2hBus.fire) {
        rState := State.IDLE
      }
    }
    //is (State.BUS_WR) {
    //}
  }
}

case class MeltedMoonIoctl(
  cfg: MeltedMoonConfig
) extends Bundle {
  def ioctlDW = cfg.ioctlSpinalDw - 1
	// ARM -> FPGA download
	val download = (
	  in(Bool()) // signal indicating an active download
	)
	val index = (
	  in(UInt(15 + 1 bits))
	)
	  // menu index used to upload the file
	val wr = (
	  in(Bool())
	)
	val addr = (
	  in(UInt(26 + 1 bits))
	  // in WIDE mode address will be incremented by 2
	)
	val dout = (
	  in(UInt(ioctlDW + 1 bits))
	)
	val upload = (
	  in(Bool()) // signal indicating an active upload
	)
	val upload_req = (
	  out(Bool())
	  // request to save (must be supported on HPS side for specific core)
	)
	val upload_index = (
	  out(UInt(7 + 1 bits))
	)
	val din = (
	  out(UInt(ioctlDW + 1 bits))
	)
	val rd = (
	  in(Bool())
	)
	val file_ext = (
	  in(UInt(31 + 1 bits))
	)
	val myWait = (
	  out(Bool()) // rename this to `ioctl_wait`
	)
}

case class MeltedMoonIo(
  //clkRate: HertzNumber,
  cfg: MeltedMoonConfig,
) extends Bundle {
  //--------
  //val mainLogicReset = in(Bool())
	//val regFileWriteEnable = (
	//  cfg.dbgExposeExtrasAtRegFileWrite
	//) generate (
	//  out(Bool())
	//)
	val softReset = in(Bool())
	val pllLocked = in(Bool())
	val cpuDbgInfo = (
	  cfg.dbgExposeExtrasAtRegFileWrite
	) generate (
	  out(SnowHouseDebugInfo(
	    cfg=cfg.cpuCfg.shCfg
	  ))
	)
	val regFileWriteActive = (
	  cfg.dbgExposeExtrasAtRegFileWrite
	) generate (
	  out(Bool())
	)

  val outpChar = out(Flow(UInt(8 bits)))

	//val regFileWriteAddr = (
	//  cfg.dbgExposeExtrasAtRegFileWrite
	//) generate (
	//  out(UInt(log2Up(16) bits))
	//)
	//val regFileWriteData = (
	//  cfg.dbgExposeExtrasAtRegFileWrite
	//) generate (
	//  out(UInt(cfg.cpuCfg.mainWidth bits))
	//)
	//val laggingRegPcAtRegFileWrite = (
	//  cfg.dbgExposeExtrasAtRegFileWrite
	//) generate (
	//  out(UInt(cfg.cpuCfg.mainWidth bits))
	//)
	val ioctl = (
    !cfg.dbgUseLcvBusMem
	) generate (
	  MeltedMoonIoctl(
      cfg=cfg
    )
  )
  //--------
  val sdram = (
    !cfg.dbgUseLcvBusMem
  ) generate (
    LcvBusSdramIo(
      cfg=cfg.sdramCtrlCfg
    )
  )
  //--------
  def ioctlDW = cfg.ioctlSpinalDw - 1
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

//case class MeltedMoonSdramIoctlIo(
//  cfg: MeltedMoonConfig
//) extends Bundle {
//	//--------
//	val ioctl = (
//	  MeltedMoonIoctl(
//      cfg=cfg
//    )
//  )
//
//  val sdram = (
//    LcvBusSdramIo(
//      cfg=cfg.sdramCtrlCfg
//    )
//  )
//
//  val bus = (
//    slave(LcvBusIo(
//    ))
//  )
//  //--------
//}
//
//case class MeltedMoonSdramIoctl(
//  cfg: MeltedMoonConfig
//) extends Component {
//  val io = MeltedMoonSdramIoctlIo(cfg=cfg)
//  val mySdramCtrl = (
//    LcvBusSdramCtrl(
//      cfg=cfg.sdramCtrlCfg
//    )
//  )
//  mySdramCtrl.io.sdram <> io.sdram
//}

case class MeltedMoon(
  cfg: MeltedMoonConfig
) extends Component {
  //--------
  val io = MeltedMoonIo(cfg=cfg)
  noIoPrefix()
  //--------
  val cartDownload = (
    (
      (
        //(
        //  (~io.ioctl.index(5 downto 0).orR)
        //  && (io.ioctl.index(7 downto 6) === 0x0)
        //)
        //||
        (
          io.ioctl.index(5 downto 0) === 0x1
        )
      )
      && io.ioctl.download
    )
    //|| !io.pllLocked
  )
  val myTempDownloadCond = (
    io.ioctl.download
    //cartDownload
  )
  //val myTempRstArea =
  //  new ResetArea(
  //    io.softReset,
  //    cumulative=true,
  //  )
  //{
  //  val myTempRstCondMain = (
  //    RegNext(
  //      //io.ioctl.download,
  //      //cartDownload,
  //      myTempDownloadCond,
  //      init=False
  //    )
  //    || RegNextWhen(
  //      False,
  //      cond=(
  //        //io.ioctl.download
  //        //cartDownload
  //        myTempDownloadCond
  //      ),
  //      init=True,
  //    )
  //  )
  //  ////val myTempRstCond = (
  //  ////  myTempRstCondMain
  //  ////  //RegNext(
  //  ////  //  //io.ioctl.download,
  //  ////  //  cartDownload,
  //  ////  //  init=False
  //  ////  //)
  //  ////  || RegNextWhen(
  //  ////    False,
  //  ////    cond=(
  //  ////      //io.ioctl.download
  //  ////      cartDownload
  //  ////    ),//cartDownload,
  //  ////    init=True,
  //  ////  )
  //  ////)
  //  val myTempRstCondRisingEdge = (
  //    RegNext(
  //      rose(
  //        //io.ioctl.download
  //        //cartDownload
  //        myTempDownloadCond
  //      ),
  //      init=False
  //    )
  //  )
  //}
  //def myTempRstCondMain = (
  //  myTempRstArea.myTempRstCondMain
  //)

  //val myTempRstCondPll = (
  //  RegNext(
  //    rose(
  //      //io.ioctl.download
  //      cartDownload
  //    ),
  //    init=False
  //  )
  //  || !io.pllLocked
  //)

  //val rRstCnt = (
  //  Reg(SInt(17 bits))
  //  init(-1)
  //)
  //when (
  //  RegNext(
  //    rose(
  //      //io.ioctl.download
  //      cartDownload
  //    ),
  //    init=False
  //  )
  //  && rRstCnt.msb
  //) {
  //  rRstCnt := S(17 bits, 16 -> False, default -> True)
  //} elsewhen (!rRstCnt.msb) {
  //  rRstCnt := rRstCnt - 1 
  //}
  //val myCartDownloadCond = (
  //  !rRstCnt.msb
  //  //RegNext(
  //  //  io.ioctl.download,
  //  //  //cartDownload,
  //  //  init=False,
  //  //)
  //  //|| RegNextWhen(
  //  //  False,
  //  //  cond=cartDownload,//io.ioctl.download,//cartDownload,
  //  //  init=True,
  //  //)
  //)
  //val mySdramCtrl = (
  //  LcvBusSdramCtrl(
  //    cfg=cfg.sdramCtrlCfg
  //  )
  //)
  //mySdramCtrl.io.sdram <> io.sdram
  val myTestArea =
    //new Area
    //new ResetArea(
    //  //cartDownload
    //  //rose(myCartDownloadCond),
    //  //myCartDownloadCond,
    //  //rose(cartDownload),
    //  //!io.pllLocked,
    //  //myTempRstCondRisingEdge || !io.pllLocked,
    //  //myTempRstCondRisingEdge,
    //  rose(io.softReset),
    //  cumulative=true//false//true
    //)
    //--------
    // TODO: try `new Area` here...
    //--------
    new Area
  {
    val mySdramCtrl = (
      LcvBusSdramCtrl(
        cfg=cfg.sdramCtrlCfg
      )
    )
    mySdramCtrl.io.sdram <> io.sdram
    //mySdramCtrl.io.softReset := rose(io.softReset)
  }
  def mySdramCtrl = myTestArea.mySdramCtrl
  //mySdramCtrl.io.sdram <> io.sdram
  //def mySdramCtrlHostIdxFbDcache = 1//0//1//0//1//0//2//1////2 
  //def mySdramCtrlHostIdxFbInit = 1//2//1
  def mySdramCtrlHostIdxIoctl = 0//2//1//0//2//1//0//1//2//0//1//0//2//1//0//1 //
  def mySdramCtrlHostIdxFbDcache = 1//0//1//0//1//0//1//0//1//0//2//1////2 
 // def mySdramCtrlHostIdxFbInit = 2//1//2//1
  //def mySdramCtrlHostIdxIoctl = 2//1//0//2//1//0//1//2//0//1//0//2//1//0//1 //
  def mySdramCtrlHostIdxFbInit = 2//1//2//1//2//1
  //def mySdramCtrlHostIdxIoctl = 2//1//0//1 //
  def mySdramCtrlHostIdxIcache = 3//2//3//2//3//2
  def mySdramCtrlHostIdxNonFbDcache = 4//3//4//3//4//3
  def limMySdramCtrlHostIdx = 5//4//5//4//5//4

  //val rSdramCtrlArbState = Reg(Bool(), init=False)
  //val rSdramCtrlArbRstCnt = (
  //  Reg(UInt(17 bits))
  //  init(0x0)
  //)
  //when (io.softReset) {
  //}

  val mySdramCtrlBusArbiterArea =
    //new ResetArea(
    //  //RegNext(rose(io.ioctl.download), init=False),
    //  //myTempRstCondRisingEdge,
    //  //myTempRstCondRisingEdge || !io.pllLocked,
    //  rose(io.softReset),
    //  //io.softReset,
    //  cumulative=true,
    //)
    new Area
  {
    val arbiter =
    //val mySdramCtrlBusArbiter = 
    LcvBusArbiter(
      cfg=LcvBusArbiterConfig(
        busCfg=cfg.sdramCtrlCfg.busCfg,
        numHosts=limMySdramCtrlHostIdx, // add 1 for the icache
        kind=(
          LcvBusArbiterKind.Priority
          //LcvBusArbiterKind.RoundRobin
        ),
      )
    )
    arbiter.io.en := (
      True
      //!myTempRstCondRisingEdge
      //!myTempRstCondMain // `cpuArea`'s next reset signal
    )
    arbiter.io.softReset := (
      rose(io.softReset)
    )
    mySdramCtrl.io.bus <-/< arbiter.io.dev
  }
  def mySdramCtrlBusArbiter = (
    mySdramCtrlBusArbiterArea.arbiter
  )

  def mySdramCtrlFbDcacheHost = (
    //sdramArea.mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxFbDcache)
    mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxFbDcache)
  )
  def mySdramCtrlFbInitHost = (
    //sdramArea.mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxFbInit)
    mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxFbInit)
  )
  def mySdramCtrlIoctlHost = (
    //sdramArea.mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxIoctl)
    mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxIoctl)
  )
  def mySdramCtrlIcacheHost = (
    //sdramArea.mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxIcache)
    mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxIcache)
  )
  def mySdramCtrlNonFbDcacheHost = (
    //sdramArea.mySdramCtrlBusArbiter.io.hostVec(
    //  mySdramCtrlHostIdxNonFbDcache
    //)
    mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxNonFbDcache)
  )

  val vgaArea =
    new ResetArea(
      //cartDownload,
      //rose(io.ioctl.download),
      //rose(myCartDownloadCond),
      //RegNext(rose(io.ioctl.download), init=False),
      //myTempRstCondRisingEdge,
      //myTempRstCond,
      //myTempRstCondMain,
      //myTempRstCondRisingEdge,
      //io.softReset,
      rose(io.softReset),
      //io.softReset,
      cumulative=true
    )
    //new Area
  {
    val vgaTimingInfo = cfg.vgaTimingInfo
    val lcvVgaCtrl = (
      LcvVgaCtrl(
        clkRate=cfg.clkRate,
        rgbConfig=cfg.rgbCfg,
        vgaTimingInfo=vgaTimingInfo,
        fifoDepth=(
          1
        ),
      )
    )
    lcvVgaCtrl.io.fifoFlush := False
    io.vgaPhys.setAsReg() init(io.vgaPhys.getZero)
    io.vgaVisib.setAsReg() init(io.vgaVisib.getZero)
    io.vgaPixelEn.setAsReg() init(io.vgaPixelEn.getZero)

    io.vgaPhys.hsync := lcvVgaCtrl.io.phys.hsync
    io.vgaPhys.vsync := lcvVgaCtrl.io.phys.vsync
    io.vgaVisib := lcvVgaCtrl.io.misc.visib
    io.vgaPixelEn := lcvVgaCtrl.io.misc.pixelEn

    lcvVgaCtrl.io.en := !io.softReset //True
    when (lcvVgaCtrl.io.misc.visib) {
      io.vgaPhys.col.r(2 downto 0) := 0x7
      io.vgaPhys.col.r(7 downto 3) := lcvVgaCtrl.io.phys.col.r
      io.vgaPhys.col.g(2 downto 0) := 0x7
      io.vgaPhys.col.g(7 downto 3) := lcvVgaCtrl.io.phys.col.g
      io.vgaPhys.col.b(2 downto 0) := 0x7
      io.vgaPhys.col.b(7 downto 3) := lcvVgaCtrl.io.phys.col.b
    } otherwise {
      io.vgaPhys.col := io.vgaPhys.col.getZero
    }

    val myDoVblankIrq = Bool()
    myDoVblankIrq := (
      rose(
        RegNext(
          (lcvVgaCtrl.io.misc.vpipeS === LcvVgaState.front),
          init=False,
        )
      )
    )
  }

  val fbInitArea =
    //new ResetArea(
    //  //cartDownload,
    //  //rose(io.ioctl.download),
    //  //myCartDownloadCond,
    //  //myTempRstCondRisingEdge,
    //  rose(io.softReset),
    //  //io.softReset,
    //  cumulative=true
    //)
    new Area
  {
    val vgaTimingInfo = cfg.vgaTimingInfo
    val fbSize2d = cfg.myFbCfg.fbSize2d
    val rCnt = (
      Reg(UInt(log2Up(fbSize2d.y * fbSize2d.x + 1) + 1 bits))
      init(0x0)
    )
    val myTempH2dStm = cloneOf(mySdramCtrlFbInitHost.h2dBus)
    mySdramCtrlFbInitHost.h2dBus <-/< myTempH2dStm
    mySdramCtrlFbInitHost.d2hBus.ready := True

    myTempH2dStm.valid := (
      (rCnt < ((fbSize2d.y * fbSize2d.x) >> 1))
      //|| (
      //  RegNextWhen(
      //    False,
      //    cond=(
      //      //io.ioctl.download
      //      //cartDownload
      //      myTempDownloadCond
      //    ),
      //    init=True,
      //  )
      //)
    )
    myTempH2dStm.addr := 0x0
    myTempH2dStm.addr.allowOverride
    myTempH2dStm.addr(cfg.fbAddrSliceHi) := True
    myTempH2dStm.addr(rCnt.high + 2 downto 2) := rCnt
    myTempH2dStm.byteEn := (
      U(myTempH2dStm.byteEn.getWidth bits, default -> True)
    )
    myTempH2dStm.data := (
      U(
        myTempH2dStm.data.getWidth bits,
        // two blank pixels
        31 -> False,
        15 -> False,
        default -> True//False//True
      )
    )
    myTempH2dStm.src := 0x0
    myTempH2dStm.isWrite := True
    //when (myTempH2dStm.fire) {
    //  rCnt := rCnt + 1
    //}
    when (rose(io.softReset)) {
      rCnt := 0x0
    } elsewhen (myTempH2dStm.fire) {
      rCnt := rCnt + 1
    }
    myTempH2dStm.burstFirst := (
      myTempH2dStm.addr(5 downto 2) === 0x0
    )
    myTempH2dStm.burstLast := (
      myTempH2dStm.addr(5 downto 2) === 0xf
    )
    myTempH2dStm.burstCnt := 15
  }

  //io.ioctl.myWait := True
  //--------
  val fbAndDcacheArea = 
    new Area
    //new ResetArea(
    //  //myInnerResetCond,
    //  //myMainResetCond,
    //  //cartDownload,
    //  //io.ioctl.download,
    //  //rose(io.ioctl.download),
    //  //rose(io.ioctl.download),
    //  //rose(myCartDownloadCond),
    //  //RegNext(rose(io.ioctl.download), init=False),
    //  //myTempRstCondRisingEdge,
    //  //myTempRstCond,
    //  //myTempRstCondMain,
    //  rose(io.softReset),
    //  //io.softReset,
    //  cumulative=true//false//true//false//true
    //)
  {
    //--------
    //--------
    val myFbDcache = (
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
      ))
    )
    //val myFbDCacheLoBusClone = cloneOf(myFbDcache.io.loBus)
    //myFbDcache.io.loBus

    mySdramCtrlFbDcacheHost <-/< myFbDcache.io.hiBus
    //--------
    val myFbCtrl = LcvBusFramebufferCtrl(
      cfg=(
        cfg.myFbCfg
      )
    )
    vgaArea.lcvVgaCtrl.io.push <-/< myFbCtrl.io.pop
    //--------
    def myFbArbiterHostIdxFbCtrl = 0
    //def myFbArbiterHostIdxBlockCpu = 1
    def myFbArbiterHostIdxCpu = 1//2//1
    def limMyFbArbiterHostIdx = 2//3//2

    val myFbArbiter = LcvBusArbiter(
      cfg=LcvBusArbiterConfig(
        busCfg=(
          cfg.myFbCtrlMmapCfg.busCfg
        ),
        numHosts=limMyFbArbiterHostIdx,
        kind=(
          LcvBusArbiterKind.Priority
          //LcvBusArbiterKind.RoundRobin
        )
      )
    )
    myFbArbiter.io.en := (
      True
      //!myTempRstCondRisingEdge
      //!myTempRstCondMain // `cpuArea`'s next reset signal
    )
    myFbArbiter.io.softReset := (
      rose(io.softReset)
    )
    def myFbArbFbCtrlHost = (
      myFbArbiter.io.hostVec(myFbArbiterHostIdxFbCtrl)
    )
    //def myFbArbBlockCpuHost = (
    //  myFbArbiter.io.hostVec(myFbArbiterHostIdxBlockCpu)
    //)
    def myFbArbCpuHost = (
      myFbArbiter.io.hostVec(myFbArbiterHostIdxCpu)
    )

    myFbArbFbCtrlHost <-/< myFbCtrl.io.bus
    //--------
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
    //--------
  }
  //--------
  val myCpuAreaResetWire = (
    //myTempRstCondMain || 
    io.softReset
    //rose(io.softReset)
    || (
      RegNextWhen(
        False,
        cond=io.softReset,
        init=True
      )
    )
  )
  val cpuArea =
    //new Area
    new ResetArea(
      //myTempRstCond,
      //myTempRstCondMain,
      //myCpuAreaResetWire,
      //rose(io.softReset),
      //io.softReset,
      myCpuAreaResetWire,
      cumulative=true//false//true
    )
  {
    //--------
    val cpu = SnowHouseCpuWithoutRam(program=cfg.testProgram.program)

    //if (cfg.dbgUseLcvBusMem) {
    //  for (idx <- 0 until myCpuDbusWriteSearchArr.size) {
    //    val myAddr = myCpuDbusWriteSearchArr(idx).addr
    //    val myData = myCpuDbusWriteSearchArr(idx).data

    //    myCpuDbusWriteSearchCmpEqVec(idx) := (
    //      cpu.io.lcvDbus.h2dBus.fire
    //      && cpu.io.lcvDbus.h2dBus.isWrite
    //      && cpu.io.lcvDbus.h2dBus.addr === myAddr
    //      && (myData match {
    //        case Some(data) => (
    //          cpu.io.lcvDbus.h2dBus.data === data
    //        )
    //        case None => (
    //          True
    //        )
    //      })
    //    )
    //    //when ((myCpuDbusWriteSearchCmpEqVec(idx))) {
    //    //  report(Seq(
    //    //    s"myCpuDbusWriteSearchCmpEqVec(${idx}): ",
    //    //    cpu.io.lcvDbus.h2dBus.addr,
    //    //    " ",
    //    //    cpu.io.lcvDbus.h2dBus.data,
    //    //  ))
    //    //}
    //  }
    //}
    //--------
    val irqCtrl = LcvBusIrqCtrl(
      cfg=LcvBusIrqCtrlConfig(
        busCfg=cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg.loBusCfg,
        depth=2,
      )
    )
    cpu.io.idsIraIrq.nextValid := (
      //!io.softReset
      //!myCpuAreaResetWire
      //&& 
      irqCtrl.io.dstIrq.nextValid
    )
    irqCtrl.io.dstIrq.ready := (
      //!io.softReset
      //!myCpuAreaResetWire
      //&& 
      cpu.io.idsIraIrq.ready
    )

    def myDoVblankIrq = vgaArea.myDoVblankIrq
    irqCtrl.io.srcIrqVec(0) := (
      //!io.softReset
      //!myCpuAreaResetWire
      //&& 
      myDoVblankIrq
    )

    val myTimerIrqOverflow = (
      floor((cfg.clkRate / (1.0 kHz)).toDouble).toInt
    )
    val rTimerIrqCnt = (
      Reg(UInt(log2Up(myTimerIrqOverflow + 1) + 1 bits))
      init(0x0)
    )
    when (rTimerIrqCnt < myTimerIrqOverflow - 1) {
      rTimerIrqCnt := rTimerIrqCnt + 1
      irqCtrl.io.srcIrqVec(1) := False
    } otherwise {
      rTimerIrqCnt := 0x0
      irqCtrl.io.srcIrqVec(1) := True//!myCpuAreaResetWire //True
    }
    //--------
    val icache = LcvBusCache(cfg=cfg.cpuCfg.shCfg.subCfg.lcvIbusEtcCfg)
    val myTempCpuLcvIbusD2hStm = cloneOf(cpu.io.lcvIbus.d2hBus)
    val myHistCpuIbusD2hFire = History[Bool](
      that=True,
      length=2,
      when=cpu.io.lcvIbus.d2hBus.fire,
      init=False,
    )
    icache.io.loBus.h2dBus << cpu.io.lcvIbus.h2dBus
    icache.io.loBus.d2hBus.translateInto(myTempCpuLcvIbusD2hStm)(
      dataAssignment=(outp, inp) => {
        outp := inp
        val tempCond = (
          inp.src
          === (
            RegNextWhen(
              inp.src,
              cond=icache.io.loBus.d2hBus.fire,
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
          )
        ) {
          outp.data := outp.data.getZero
          when (inp.src === 0x3) {
            rState := True
          }
        }
      }
    )
    cpu.io.lcvIbus.d2hBus <-/< myTempCpuLcvIbusD2hStm
    mySdramCtrlIcacheHost <-/< icache.io.hiBus
    //--------
    val myDbusSlicer = LcvBusSlicer(
      cfg=LcvBusSlicerConfig(
        mmapCfg=(
          //cfg.myFbDbusSlicerMmapCfg
          cfg.myDbusSlicerMmapCfg
        ),
        maxNumOutstandingTxns=(
          // if this doesn't work, try increasing it.
          // It has been reduced to shrink the size of the counter for fmax
          // purposes
          4
        ),
      )
    )

    val myFbCpuHostClone = cloneOf(fbAndDcacheArea.myFbArbCpuHost)

    fbAndDcacheArea.myFbArbCpuHost <-/< myFbCpuHostClone

    def mySlicedNonFbDcacheHost = myDbusSlicer.io.devVec(
      //(cfg.myFbCtrlMmapCfg.optAddrSliceVal.get + 1) % 2
      //0
      cfg.myNonFbSdramAddrSliceVal
    )
    def mySlicedFbDcacheHost = myDbusSlicer.io.devVec(
      //1
      //cfg.myFbCtrlMmapCfg.optAddrSliceVal.get
      //1
      cfg.myFbAddrSliceVal
    )
    def mySlicedIoIrqCtrlHost = myDbusSlicer.io.devVec(
      cfg.myIoIrqCtrlAddrSliceVal
    )
    def mySlicedIoDbgPrintHost = myDbusSlicer.io.devVec(
      cfg.myIoDbgPrintAddrSliceVal
    )

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

    //val myFbCpuHostClone = cloneOf(myFbArbCpuHost)
    //myDbusSlicer.io.host << cpu.io.lcvDbus
    myDbusSlicer.io.host.h2dBus <-/< cpu.io.lcvDbus.h2dBus
    cpu.io.lcvDbus.d2hBus << myDbusSlicer.io.host.d2hBus
    //--------
    // BEGIN: later `myNonFbDcache`
    //0x44a90
    val myNonFbDcache = (
      LcvBusCache(cfg=cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg),
    )
    myNonFbDcache.io.loBus << mySlicedNonFbDcacheHost
    mySdramCtrlNonFbDcacheHost <-/< myNonFbDcache.io.hiBus
    // END: later `myNonFbDcache`
    //--------
    if (cfg.dbgExposeExtrasAtRegFileWrite) {
      io.regFileWriteActive := False
      io.cpuDbgInfo := cpu.io.dbgInfo
      when (
        cpu.io.regFileWriteEnable
        && !cpu.io.shouldIgnoreInstrAtRegFileWrite
      ) {
        io.regFileWriteActive := True
      }
    }
    //--------

    irqCtrl.io.bus <-/< mySlicedIoIrqCtrlHost 

    val dbgPrint = MeltedMoonDbgPrint(cfg=cfg)
    dbgPrint.io.bus <-/< mySlicedIoDbgPrintHost
    //dbgPrint.io.outpChar.simPublic
    io.outpChar := dbgPrint.io.outpChar
  }
  //when (
  //  myCpuAreaResetWire
  //  //|| RegNextWhen(
  //  //  False,
  //  //  cond=myCpuAreaResetWire,
  //  //  init=True,
  //  //)
  //) {
  //  //myFbArbBlockCpuHost.h2dBus.valid := True
  //  mySdramCtrlIcacheHost.h2dBus.valid := False
  //  mySdramCtrlIcacheHost.d2hBus.ready := True
  //  mySdramCtrlNonFbDcacheHost.h2dBus.valid := False
  //  mySdramCtrlNonFbDcacheHost.d2hBus.ready := True
  //  fbAndDcacheArea.myFbArbCpuHost.h2dBus.valid := False
  //  fbAndDcacheArea.myFbArbCpuHost.d2hBus.ready := True
  //}

  //val myIoctlAreaResetWire = (
  //  //myTempRstCondRisingEdge
  //  rose(io.softReset)
  //)

  val ioctlArea =
    //new ResetArea(
    //  myIoctlAreaResetWire,
    //  //rose(io.softReset),
    //  //io.softReset,
    //  cumulative=true,//false//true,
    //)
    new Area
  {
    io.ioctl.upload_req := False
    io.ioctl.upload_index := 0x0
    io.ioctl.din := 0x0
    //io.ioctl.myWait := False
    case class MyIoctlPayload(
      dataWidth: Int,
    ) extends Bundle {
      val addr = (
        //cloneOf(io.ioctl.addr)
        UInt(cfg.sdramCtrlCfg.busCfg.addrWidth bits)
      )
      val data = UInt(dataWidth bits)
      val isWrite = Bool()
      val byteEn = UInt(cfg.sdramCtrlCfg.busCfg.byteEnWidth bits)
    }
    val myIoctlRecvPushStm = (
      Vec[Stream[MyIoctlPayload]](
        List[Stream[MyIoctlPayload]](
          Stream(MyIoctlPayload(cfg.ioctlSpinalDw)),
          Stream(MyIoctlPayload(cfg.ioctlSpinalDw)),
          Stream(MyIoctlPayload(cfg.sdramCtrlCfg.busCfg.dataWidth)),
          Stream(MyIoctlPayload(cfg.sdramCtrlCfg.busCfg.dataWidth)),
        )
      )
    )
    val myIoctlPushCntWidth = (
      log2Up(
        cfg.sdramCtrlCfg.busCfg.dataWidth
        / cfg.ioctlSpinalDw
      ).toInt
    )
    myIoctlRecvPushStm(1) <-< myIoctlRecvPushStm.head
    myIoctlRecvPushStm(1).translateInto(myIoctlRecvPushStm(2))(
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
        switch (inp.addr(myIoctlPushCntWidth + 1 - 1 downto 1)) {
          for (idx <- 0 until (1 << myIoctlPushCntWidth)) {
            is (idx) {
              //println(
              //  (idx + 1) * inp.data.getWidth - 1
              //  downto idx * inp.data.getWidth
              //)
              outp.data(
                (idx + 1) * inp.data.getWidth - 1
                downto idx * inp.data.getWidth
              ) := inp.data
              if (idx == 0) {
                //when (rIoctlRecvPushLaggingAddrCond) {
                //  outp.addr := inp.addr - myIoctlLaggingAddr
                //} otherwise {
                //  outp.addr := outp.addr.getZero
                //}
                //outp.addr := inp.addr - 2
                outp.addr := inp.addr //- 2
                outp.isWrite := inp.isWrite//True//False
                outp.byteEn := (
                  //U(outp.byteEn.getWidth bits, default -> True)
                  0x3
                  //0x0
                )
              } else {
                outp.addr := inp.addr //- 2
                outp.isWrite := inp.isWrite//True//inp.isWrite
                outp.byteEn := (
                  //U(outp.byteEn.getWidth bits, default -> True)
                  0xc
                )
              }
            }
          }
        }
      }
    )
    //myIoctlRecvPushStm.last <-/< myIoctlRecvPushStm(1).throwWhen(
    //  !myIoctlRecvPushStm.last.isWrite
    //)
    myIoctlRecvPushStm.last <-< myIoctlRecvPushStm(2)

    myIoctlRecvPushStm.head.valid := (
      //RegNext(
        //cartDownload && io.ioctl.wr,
        //io.ioctl.download && io.ioctl.wr//,
        //cartDownload && io.ioctl.wr//,
        //cartDownload
        myTempDownloadCond
        //|| RegNextWhen(
        //  False,
        //  cond=(
        //    //cartDownload
        //    myTempDownloadCond
        //  ),
        //  init=True,
        //)
        //myCartDownloadCond
      //  init=False
      //)
      //&& io.ioctl.wr
    )
    myIoctlRecvPushStm.head.data := (
      //RegNext(
        Mux(
          myTempDownloadCond && io.ioctl.wr,
          io.ioctl.dout,
          RegNext(myIoctlRecvPushStm.head.data)
        )
      //)
      //init(0x0)
    )
    myIoctlRecvPushStm.head.addr := (
      //RegNext(
        //io.ioctl.addr.resize(myIoctlRecvPushStm.head.addr.getWidth),
        Mux(
          //cartDownload,
          myTempDownloadCond && io.ioctl.wr,
          io.ioctl.addr.resize(myIoctlRecvPushStm.head.addr.getWidth),
          RegNext(myIoctlRecvPushStm.head.addr)
          //myIoctlRecvPushStm.head.addr.getZero,
        )
      //)
      //init(0x0)
    )
    myIoctlRecvPushStm.head.isWrite := (
      //io.ioctl.wr
      //RegNext(
      //  cartDownload && io.ioctl.wr,
      //  init=False
      //)
      True
      //io.ioctl.wr
      //True
    )
    io.ioctl.myWait := (
      //cartDownload
      //&& io.ioctl.wr
      //&& 
      //fbInitArea.myTempH2dStm.valid
      //|| 
      //RegNext(cartDownload, init=False)
      //RegNext(io.ioctl.download, init=False)
      //&& 

      //myIoctlRecvPushStm.head.valid

      //io.ioctl.download
      //&&

      //cartDownload
      //myIoctlRecvPushStm.head.valid
      //cartDownload

      //myTempDownloadCond
      //&& 
      (
        //ClockDomain.current.readResetWire
        //|| 
        !myIoctlRecvPushStm.head.ready
        //|| rose(io.softReset)
        //!myIoctlRecvPushStm.head.fire
      )
    )
    //val myIoctlRecvPopStm = cloneOf(myIoctlRecvFifo.io.pop)
    val myIoctlRecvPopStm = Stream(
      MyIoctlPayload(
        cfg.sdramCtrlCfg.busCfg.dataWidth
        //cfg.ioctlSpinalDw bits
      )
    )
    myIoctlRecvPopStm <-< myIoctlRecvPushStm.last//myIoctlRecvFifo.io.pop
    myIoctlRecvPopStm.translateInto(mySdramCtrlIoctlHost.h2dBus)(
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
        outp.src := outp.src.getZero

        outp.burstCnt := outp.burstCnt.getZero
        outp.burstFirst := False
        outp.burstLast := False
      }
    )
    //mySdramCtrlIoctlHost.h2dBus <-/< sdramInitFifo.io.pop
    mySdramCtrlIoctlHost.d2hBus.ready := True
  }
  //when (
  //  !ClockDomain.current.readResetWire
  //  && myCartDownloadCond
  //) {
  //  fbAndDcacheArea.myFbArbCpuHost.d2hBus.ready := True
  //  mySdramCtrlIcacheHost.d2hBus.ready := True
  //  mySdramCtrlNonFbDcacheHost.d2hBus.ready := True
  //}
  //--------
  //when (
  //  //myCartDownloadCond
  //  //myTempRstCondRisingEdge
  //  myIoctlAreaResetWire
  //  //|| RegNextWhen(
  //  //  False,
  //  //  cond=myIoctlAreaResetWire,
  //  //  init=True,
  //  //)
  //) {
  //  //mySdramCtrlIoctlHost.h2dBus.valid := False
  //  //mySdramCtrlIoctlHost.d2hBus.ready := False

  //  io.ioctl.upload_req := False
  //  io.ioctl.upload_index := 0x0
  //  io.ioctl.din := 0x0
  //  io.ioctl.myWait := True
  //  //mySdramCtrl.io.bus.d2hBus.ready := True
  //  //mySdramCtrl.io.bus.h2dBus.valid := False
  //}
  //when (rose(myCartDownloadCond)) {
  //  io.ioctl.myWait := True
  //}

}
//case class MeltedMoonOld(
//  cfg: MeltedMoonConfig,
//) extends Component {
//  //--------
//  val io = MeltedMoonIo(cfg=cfg)
//  noIoPrefix()
//  //--------
//  val myMainResetCond = (
//    ////io.mainLogicReset,
//    ////RegNextWhen(
//    ////  False,
//    ////  cond=(
//    //    RegNextWhen(
//    //      False,
//    //      cond=(
//    //        //io.ioctl_download
//    //        cartDownload
//    //      ),
//    //      init=True,
//    //    )
//    //    //!rCartDownloadState
//    //    //|| 
//    //    || myInnerResetCond
//
//    //    //|| sdramInitFifo.io.pop.valid
//    //    //|| io.ioctl_download
//    //    //|| sdramInitFifo.io.pop.valid
//    ////  ),
//    ////  init=True
//    ////)
//    RegNext(
//      (
//        //cartDownload
//        io.ioctl_download
//        //rose(io.ioctl_download)
//      ),
//      init=False
//    )
//    //|| rose(io.ioctl_download)
//    //|| ClockDomain.current.readResetWire
//    || (
//      RegNextWhen(
//        False,
//        cond=(
//          //cartDownload
//          io.ioctl_download
//        ),
//        init=True,
//      )
//    )
//  )
//  val mySdramCtrlArea = 
//    //new ResetArea(
//    //  ///*rose*/(myInnerResetCond),
//    //  //cartDownload,
//    //  myMainResetCond,
//    //  //io.ioctl_download,
//    //  cumulative=true//true//false//true
//    //)
//    //new ResetArea(
//    //  //rose(myInnerResetCond),
//    //  rose(io.ioctl_download),
//    //  cumulative=true
//    //)
//    new Area
//  {
//    val mySdramCtrl = (
//      !cfg.dbgUseLcvBusMem
//    ) generate (
//      LcvBusSdramCtrl(
//        cfg=cfg.sdramCtrlCfg
//      )
//    )
//    if (!cfg.dbgUseLcvBusMem) {
//      mySdramCtrl.io.sdram <> io.sdram
//    }
//    val myDbgLcvBusMem = (
//      cfg.dbgUseLcvBusMem
//    ) generate {
//      val depth = 1 << (27 - 2) // 128 MiB
//
//      val myInitBigInt = {
//        //val depth = 1 << (16 - 4)
//        val tempArr = new ArrayBuffer[BigInt]()
//        tempArr ++= cfg.program.outpArr.view
//        while (tempArr.size < depth) {
//          tempArr += BigInt(0)
//        }
//        //val programSize = tempArr.size
//        //for (idx <- programSize until (1 << (16 - 4))) {
//        //  if (idx < /*1024*/0x800) {
//        //    //println(
//        //    //  s"idx < 0x800: ${idx}"
//        //    //)
//        //    //tempArr += BigInt(idx)
//        //    tempArr += BigInt(0)
//        //  } else {
//        //    //println(
//        //    //  s"idx < 0x800: ${idx}"
//        //    //)
//        //    //tempArr += BigInt(0)
//        //  }
//        //  //tempArr += BigInt(0)
//        //}
//        tempArr
//        //for (elem <- program.outpArr.view) {
//        //  tempArr +=
//        //}
//        //program.outpArr
//      }
//      LcvBusMem(
//        cfg=LcvBusMemConfig(
//          busCfg=cfg.sdramCtrlCfg.busCfg,
//          depth=depth,
//          initBigInt=Some(myInitBigInt),
//        )
//      )
//    }
//    def mySdramCtrlHostIdxIoctl = 0//1//0//2//1//0//1 //
//    def mySdramCtrlHostIdxFbDcache = 1//0//1//0//2//1////2 
//    //def mySdramCtrlHostIdxFbInit = 2//1
//    //def mySdramCtrlHostIdxIoctl = 2//1//0//1 //
//    def mySdramCtrlHostIdxIcache = 2//3//2
//    def mySdramCtrlHostIdxNonFbDcache = 3//4//3
//    def limMySdramCtrlHostIdx = 4//5//4
//
//    val mySdramCtrlBusArbiter = LcvBusArbiter(
//      cfg=LcvBusArbiterConfig(
//        busCfg=cfg.sdramCtrlCfg.busCfg,
//        numHosts=limMySdramCtrlHostIdx, // add 1 for the icache
//        kind=(
//          LcvBusArbiterKind.Priority
//          //LcvBusArbiterKind.RoundRobin
//        ),
//      )
//    )
//    //mySdramCtrl.io.bus <-/< mySdramCtrlBusArbiter.io.dev
//    //val mySdramDeburster = LcvBusDeburster(
//    //  cfg=LcvBusDebursterConfig(
//    //    loBusCfg=(
//    //      cfg.sdramCtrlCfg.busCfg
//    //      //LcvBusConfig(
//    //      //  mainCfg=cfg.sdramCtrlCfg.busCfg.mainCfg.mkCopyWithoutAllowingBurst()
//    //      //)
//    //    )
//    //  )
//    //)
//    mySdramCtrlBusArbiter.io.en := True
//    //mySdramDeburster.io.loBus <-/< mySdramCtrlBusArbiter.io.dev
//
//    //val myTempSdramDebursterHiBus = cloneOf(mySdramDeburster.io.hiBus)
//    //myTempSdramDebursterHiBus <-/< mySdramDeburster.io.hiBus
//    //myTempSdramDebursterHiBus.h2dBus.translateInto(
//    //  mySdramCtrl.io.bus.h2dBus
//    //)(
//    //  dataAssignment=(outp, inp) => {
//    //    outp.mainNonBurstInfo := inp.mainNonBurstInfo
//    //    outp.mainBurstInfo := outp.mainBurstInfo.getZero
//    //  }
//    //)
//    //mySdramCtrl.io.bus.d2hBus.translateInto(
//    //  myTempSdramDebursterHiBus.d2hBus
//    //)(
//    //  dataAssignment=(outp, inp) => {
//    //    outp.mainNonBurstInfo := inp.mainNonBurstInfo
//    //  }
//    //)
//
//    if (!cfg.dbgUseLcvBusMem) {
//      mySdramCtrl.io.bus <-/< mySdramCtrlBusArbiter.io.dev
//    } else {
//      myDbgLcvBusMem.io.bus <-/< mySdramCtrlBusArbiter.io.dev
//    }
//
//    def mySdramCtrlFbDcacheHost = (
//      mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxFbDcache)
//    )
//    //def mySdramCtrlFbInitHost = (
//    //  mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxFbInit)
//    //)
//    def mySdramCtrlIoctlHost = (
//      mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxIoctl)
//    )
//    def mySdramCtrlIcacheHost = (
//      mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxIcache)
//    )
//    def mySdramCtrlNonFbDcacheHost = (
//      mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxNonFbDcache)
//    )
//    //val sdramInitFifo = (
//    //  !cfg.dbgUseLcvBusMem
//    //) generate (
//    //  StreamFifo(
//    //    dataType=cloneOf(mySdramCtrlIoctlHost.h2dBus.payload),
//    //    depth=(
//    //      32,
//    //    ),
//    //    latency=2,
//    //    forFMax=true,
//    //    //pushClock=ioctlClkDomain,
//    //    //popClock=ClockDomain.current,
//    //  )
//    //)
//    if (cfg.dbgUseLcvBusMem) {
//      mySdramCtrlIoctlHost.h2dBus.valid := False
//      mySdramCtrlIoctlHost.h2dBus.payload := (
//        mySdramCtrlIoctlHost.h2dBus.payload.getZero
//      )
//      mySdramCtrlIoctlHost.d2hBus.ready := False
//    }
//  }
//  def mySdramCtrlFbDcacheHost = (
//    mySdramCtrlArea.mySdramCtrlFbDcacheHost
//  )
//  //def mySdramCtrlFbInitHost = (
//  //  mySdramCtrlArea.mySdramCtrlFbInitHost
//  //)
//  def mySdramCtrlIoctlHost = (
//    mySdramCtrlArea.mySdramCtrlIoctlHost
//  )
//  def mySdramCtrlIcacheHost = (
//    mySdramCtrlArea.mySdramCtrlIcacheHost
//  )
//  def mySdramCtrlNonFbDcacheHost = (
//    mySdramCtrlArea.mySdramCtrlNonFbDcacheHost
//  )
//
//  //--------
//
//  //--------
//  val cartDownload = (
//    Bool()
//  )
//  if (!cfg.dbgUseLcvBusMem) {
//    cartDownload := (
//      (
//        (
//          (~io.ioctl_index(5 downto 0).orR)
//          && (io.ioctl_index(7 downto 6) === 0x0)
//        )
//        || (
//          io.ioctl_index(5 downto 0) === 0x1
//        )
//      )
//      && io.ioctl_download
//      //io.ioctl_wr
//      //&& io.ioctl_download
//      //&& io.ioctl_index(5 downto 0) === 0x1
//      ////&& codeIndex
//      ////&& !codeIndex
//      ////&& (io.ioctl_index =/= 4)
//      ////&& (io.ioctl_index =/= 254)
//    )
//  } else {
//    cartDownload := (
//      RegNext(False, init=True)
//    )
//  }
//
//  //val myInnerResetCond = Bool()
//  //if (!cfg.dbgUseLcvBusMem) {
//  //  myInnerResetCond := (
//  //    io.ioctl_download
//  //    //cartDownload
//  //    || sdramInitFifo.io.pop.valid
//  //  )
//  //} else {
//  //  myInnerResetCond := False
//  //}
//  //val rCartDownloadState = Reg(Bool(), init=False)
//
//  //when (
//  //  //myMainResetCond
//  //  cartDownload
//  //) {
//  //  rCartDownloadState := True
//  //}
//  //when (
//  //  io.ioctl_download
//  //) {
//  //  rCartDownloadState := False
//  //}
//  //--------
//  //val vblankIrqFifo = StreamFifo/*CC*/(
//  //  dataType=Bool(),
//  //  depth=(
//  //    4
//  //  ),
//  //  latency=2,
//  //  forFMax=true,
//  //  //pushClock=vgaClkDomain,
//  //  //popClock=(
//  //  //  ClockDomain.current
//  //  //  //mainClkDomain
//  //  //),
//  //)
//  //--------
//  val vgaCtrlArea = 
//    new ResetArea(
//      ///*rose*/(myInnerResetCond),
//      //cartDownload,
//      //myMainResetCond,
//      //io.ioctl_download,
//      rose(io.ioctl_download),
//      cumulative=true//true//false//true
//    )
//    //new Area
//  {
//    val vgaTimingInfo = cfg.vgaTimingInfo
//    val lcvVgaCtrl = (
//      LcvVgaCtrl(
//        clkRate=cfg.clkRate,
//        //rgbConfig=physRgbConfig,
//        rgbConfig=cfg.rgbCfg,
//        vgaTimingInfo=vgaTimingInfo,
//        fifoDepth=(
//          //cfg.ctrlFifoDepth
//          //io.misc.fifoDepth
//          //32
//          1
//        ),
//      )
//    )
//    //lcvVgaCtrl.io.fifoFlush := False//rose(myInnerResetCond) // False//
//    //val calcPos = LcvVideoCalcPos(
//    //  someSize2d=vgaTimingInfo.fbSize2d
//    //)
//    ////calcPos.io.en := lcvVgaCtrl.io.en
//    //calcPos.io.en := lcvVgaCtrl.io.push.fire
//    ////when (
//    ////  calcPos.io.info.posWillOverflow.y
//    ////) {
//    ////}
//    lcvVgaCtrl.io.fifoFlush := (
//      False
//      //myInnerResetCond
//      //False//rose(myInnerResetCond) // False//
//    )
//  }
//  val otherVgaArea =
//    new ResetArea(
//      ///*rose*/(myInnerResetCond),
//      //cartDownload,
//      //myMainResetCond,
//      rose(io.ioctl_download),
//      cumulative=true//true//false//true
//    )
//    //new Area 
//  {
//    io.vgaPhys.setAsReg() init(io.vgaPhys.getZero)
//    io.vgaVisib.setAsReg() init(io.vgaVisib.getZero)
//    io.vgaPixelEn.setAsReg() init(io.vgaPixelEn.getZero)
//
//    //io.vgaPhys := lcvVgaCtrl.io.phys
//    io.vgaPhys.hsync := vgaCtrlArea.lcvVgaCtrl.io.phys.hsync
//    io.vgaPhys.vsync := vgaCtrlArea.lcvVgaCtrl.io.phys.vsync
//    io.vgaVisib := vgaCtrlArea.lcvVgaCtrl.io.misc.visib
//    io.vgaPixelEn := vgaCtrlArea.lcvVgaCtrl.io.misc.pixelEn //RegNext(lcvVgaCtrl.io.misc.pixelEn, init=False)
//
//    vgaCtrlArea.lcvVgaCtrl.io.en := True//stickySeenMyPixelPushStmValid
//    //lcvVgaCtrl.io.push << myPixelMuxStm
//    when (vgaCtrlArea.lcvVgaCtrl.io.misc.visib) {
//      io.vgaPhys.col.r(2 downto 0) := 0x7
//      io.vgaPhys.col.r(7 downto 3) := vgaCtrlArea.lcvVgaCtrl.io.phys.col.r
//      io.vgaPhys.col.g(2 downto 0) := 0x7
//      io.vgaPhys.col.g(7 downto 3) := vgaCtrlArea.lcvVgaCtrl.io.phys.col.g
//      io.vgaPhys.col.b(2 downto 0) := 0x7
//      io.vgaPhys.col.b(7 downto 3) := vgaCtrlArea.lcvVgaCtrl.io.phys.col.b
//    } otherwise {
//      io.vgaPhys.col := io.vgaPhys.col.getZero
//    }
//
//    val myDoVblankIrq = Bool()
//    //val rSavedDoVblankIrq = Reg(Bool(), init=False)
//    //val stickyDoVblankIrq = (
//    //  myDoVblankIrq
//    //  || rSavedDoVblankIrq
//    //)
//    //when (myDoVblankIrq) {
//    //  rSavedDoVblankIrq := True
//    //}
//    //when (vblankIrqFifo.io.push.fire) {
//    //  rSavedDoVblankIrq := False
//    //}
//    myDoVblankIrq := (
//      rose(
//        RegNext(
//          (vgaCtrlArea.lcvVgaCtrl.io.misc.vpipeS === LcvVgaState.front),
//          init=False,
//        )
//      )
//      //rose(
//      //  RegNext(
//      //    //!io.vgaVisib
//      //    (
//      //      !io.vgaPhys.vsync
//      //      && !io.vgaPhys.hsync
//      //    ),
//      //    init=False
//      //  )
//      //)
//    )
//    //vblankIrqFifo.io.push.valid := stickyDoVblankIrq
//    //vblankIrqFifo.io.push.payload := True
//  }
//  //--------
//  //val fbInitArea =
//  //  //new Area
//  //  new ResetArea(
//  //    //rose(myInnerResetCond), 
//  //    //cartDownload,
//  //    myMainResetCond,
//  //    cumulative=false//true
//  //  )
//  //{
//  //  val vgaTimingInfo = cfg.vgaTimingInfo
//  //  val fbSize2d = cfg.myFbCfg.fbSize2d
//  //  val rCnt = (
//  //    Reg(UInt(log2Up(fbSize2d.y * fbSize2d.x + 1) + 1 bits))
//  //    init(0x0)
//  //  )
//  //  val myTempH2dStm = cloneOf(mySdramCtrlFbInitHost.h2dBus)
//  //  mySdramCtrlFbInitHost.h2dBus <-/< myTempH2dStm
//  //  mySdramCtrlFbInitHost.d2hBus.ready := True
//
//  //  myTempH2dStm.valid := (
//  //    rCnt < ((fbSize2d.y * fbSize2d.x) >> 1)
//  //  )
//  //  myTempH2dStm.addr := 0x0
//  //  myTempH2dStm.addr.allowOverride
//  //  myTempH2dStm.addr(cfg.fbAddrSliceHi) := True
//  //  myTempH2dStm.addr(rCnt.high + 2 downto 2) := (
//  //    rCnt
//  //    //Cat(
//  //    //  rCnt,
//  //    //  False
//  //    //).asUInt.resize(myTempH2dStm.addr.getWidth)
//  //  )
//  //  myTempH2dStm.byteEn := (
//  //    U(myTempH2dStm.byteEn.getWidth bits, default -> True)
//  //  )
//  //  myTempH2dStm.data := (
//  //    //0x0
//  //    U(
//  //      myTempH2dStm.data.getWidth bits,
//  //      // two white pixels
//  //      31 -> False,
//  //      15 -> False,
//  //      default -> True
//  //    )
//  //    //myTempH2dStm.data
//  //  )
//  //  myTempH2dStm.src := 0x0
//  //  myTempH2dStm.isWrite := True
//  //  when (myTempH2dStm.fire) {
//  //    rCnt := rCnt + 1
//  //  }
//  //  //when (
//  //  //  RegNext(
//  //  //    (
//  //  //      !myTempH2dStm.valid
//  //  //      && rose(myInnerResetCond)
//  //  //    ),
//  //  //    init=False
//  //  //  )
//  //  //) {
//  //  //  // This may unnecessarily repeat clearing the framebuffer
//  //  //  rCnt := 0x0
//  //  //}
//  //  myTempH2dStm.burstFirst := (
//  //    //False
//  //    myTempH2dStm.addr(5 downto 2) === 0x0
//  //  )
//  //  myTempH2dStm.burstLast := (
//  //    //False
//  //    myTempH2dStm.addr(5 downto 2) === 0xf
//  //  )
//  //  myTempH2dStm.burstCnt := (
//  //    //0x0
//  //    //16
//  //    15
//  //  )
//  //}
//  //--------
//  val ioctlArea = (
//    !cfg.dbgUseLcvBusMem
//  ) generate (
//    new Area
//    //new ResetArea(
//    //  //rose(myInnerResetCond),
//    //  rose(io.ioctl_download),
//    //  cumulative=true
//    //)
//    //new ResetArea(
//    //  rose(cartDownload),
//    //  cumulative=true,
//    //)
//  {
//    val sdramInitFifo = (
//      !cfg.dbgUseLcvBusMem
//    ) generate (
//      StreamFifo(
//        dataType=cloneOf(mySdramCtrlIoctlHost.h2dBus.payload),
//        depth=(
//          32,
//        ),
//        latency=2,
//        forFMax=true,
//        //pushClock=ioctlClkDomain,
//        //popClock=ClockDomain.current,
//      )
//    )
//    io.ioctl_upload_req := False
//    io.ioctl_upload_index := 0x0
//    io.ioctl_din := 0x0
//    //io.ioctl_wait := False
//
//    case class MyIoctlPayload(
//      dataWidth: Int,
//    ) extends Bundle {
//      val addr = (
//        //cloneOf(io.ioctl_addr)
//        UInt(cfg.sdramCtrlCfg.busCfg.addrWidth bits)
//      )
//      val data = UInt(dataWidth bits)
//      val isWrite = Bool()
//      val byteEn = UInt(cfg.sdramCtrlCfg.busCfg.byteEnWidth bits)
//    }
//
//    val myIoctlRecvFifo = StreamFifo(
//      dataType=MyIoctlPayload(
//        cfg.sdramCtrlCfg.busCfg.dataWidth
//        //cfg.ioctlSpinalDw bits
//      ),
//      depth=8,
//      latency=2,
//      forFMax=true,
//    )
//    val myIoctlRecvPushStm = (
//      Vec[Stream[MyIoctlPayload]](
//        List[Stream[MyIoctlPayload]](
//          Stream(MyIoctlPayload(cfg.ioctlSpinalDw)),
//          Stream(MyIoctlPayload(cfg.sdramCtrlCfg.busCfg.dataWidth)),
//        )
//      )
//      //cloneOf(myIoctlRecvFifo.io.push)
//      //Stream(
//      //  UInt(
//      //    cfg.sdramCtrlCfg.busCfg.dataWidth bits
//      //    //cfg.ioctlSpinalDw bits
//      //  )
//      //)
//    )
//    //val rIoctlPushCnt = (
//    //  Reg(UInt(
//    //    //1 bits
//    //    log2Up(
//    //      cfg.sdramCtrlCfg.busCfg.dataWidth
//    //      / cfg.ioctlSpinalDw
//    //    ).toInt
//    //    bits
//    //  )) init(0x0)
//    //)
//    val myIoctlPushCntWidth = (
//      log2Up(
//        cfg.sdramCtrlCfg.busCfg.dataWidth
//        / cfg.ioctlSpinalDw
//      ).toInt
//    )
//    //val myIoctlLaggingAddr = (4 * 4 * 4 * 2)
//    //val rIoctlRecvPushLaggingAddrCond = (
//    //  RegNextWhen(
//    //    True,
//    //    cond=(
//    //      myIoctlRecvPushStm.head.addr >= myIoctlLaggingAddr
//    //    ),
//    //    init=False
//    //  )
//    //)
//    myIoctlRecvPushStm.head.translateInto(myIoctlRecvPushStm.last)(
//      dataAssignment=(outp, inp) => {
//        //outp.addr := inp.addr
//        outp.addr := (
//          RegNext(
//            outp.addr,
//            init=outp.addr.getZero
//          )
//        )
//        //outp.isWrite := inp.isWrite
//        outp.data := (
//          //outp.data.getZero
//          RegNext(
//            outp.data,
//            init=outp.data.getZero,
//          )
//        )
//        outp.byteEn := 0x0
//        switch (
//          inp.addr(myIoctlPushCntWidth + 1 - 1 downto 1)
//        ) {
//          for (idx <- 0 until (1 << myIoctlPushCntWidth)) {
//            is (idx) {
//              //outp.byteEn := (
//              //  0x3 << (idx * 2)
//              //)
//              println(
//                (idx + 1) * inp.data.getWidth - 1
//                downto idx * inp.data.getWidth
//              )
//              outp.data(
//                (idx + 1) * inp.data.getWidth - 1
//                downto idx * inp.data.getWidth
//              ) := inp.data
//              if (idx == 0) {
//                //when (rIoctlRecvPushLaggingAddrCond) {
//                //  outp.addr := inp.addr - myIoctlLaggingAddr
//                //} otherwise {
//                  outp.addr := outp.addr.getZero
//                //}
//                outp.isWrite := False
//                outp.byteEn := 0x0
//              } else {
//                outp.addr := inp.addr - 2
//                outp.isWrite := inp.isWrite
//                outp.byteEn := (
//                  U(outp.byteEn.getWidth bits, default -> True)
//                )
//              }
//            }
//          }
//        }
//      }
//    )
//    //val rSeenFirstIoctlRecvPush = Reg(Bool(), init=False)
//    myIoctlRecvFifo.io.push <-< myIoctlRecvPushStm.last.throwWhen(
//      !myIoctlRecvPushStm.last.isWrite
//    )
//    //when (
//    //  myIoctlRecvPushStm.last.fire
//    //) {
//    //  rSeenFirstIoctlRecvPush := True
//    //}
//    //when (
//    //  myIoctlRecvPushStm.last.fire
//    //) {
//    //  rIoctlPushCnt := rIoctlPushCnt + 1
//    //}
//    //val codeIndex = io.ioctl_index.orR//andR
//    //val codeDownload = io.ioctl_download && codeIndex
//    //val myIoctlRecvPushValidCond = (
//    //  //io.ioctl_wr && io.ioctl_download && cartDownload
//    //  cartDownload
//    //)
//    //myPixelMuxSel.lsb := vgaArea.stickySeenMyPixelPushStmValid
//    myIoctlRecvPushStm.head.valid := cartDownload
//    myIoctlRecvPushStm.head.data := (
//      io.ioctl_dout
//      //Mux(
//      //  cartDownload && io.ioctl_wr,
//      //  io.ioctl_dout,
//      //  RegNext(
//      //    myIoctlRecvPushStm.head.data,
//      //    init=myIoctlRecvPushStm.head.data.getZero,
//      //  )
//      //)
//    )
//    myIoctlRecvPushStm.head.addr := (
//      //Mux(
//      //  cartDownload && io.ioctl_wr,
//      //  io.ioctl_addr.resize(myIoctlRecvPushStm.head.addr.getWidth),
//      //  RegNext(
//      //    myIoctlRecvPushStm.head.addr,
//      //    init=myIoctlRecvPushStm.head.addr.getZero,
//      //  )
//      //)
//      io.ioctl_addr.resize(myIoctlRecvPushStm.head.addr.getWidth)
//    )
//    myIoctlRecvPushStm.head.isWrite := (
//      io.ioctl_wr
//      //True
//      //cartDownload
//      //&& io.ioctl_wr
//      //|| RegNextWhen(
//      //  True,
//      //  cond=cartDownload,
//      //  init=False,
//      //)
//    )
//    io.ioctl_wait := (
//      cartDownload
//      //&& io.ioctl_wr
//      && 
//      !myIoctlRecvPushStm.head.ready
//    )
//
//    val myIoctlRecvPopStm = cloneOf(myIoctlRecvFifo.io.pop)
//    myIoctlRecvPopStm <-< myIoctlRecvFifo.io.pop
//    myIoctlRecvPopStm.translateInto(sdramInitFifo.io.push)(
//      dataAssignment=(outp, inp) => {
//        outp.addr := (
//          Cat(
//            inp.addr(inp.addr.high downto 2),
//            U"2'b00",
//          ).asUInt
//        )
//        outp.data := inp.data
//        outp.isWrite := (
//          //True
//          inp.isWrite
//          //RegNextWhen(
//          //  True,
//          //  cond=myIoctlRecvPushValidCond,
//          //  init=False
//          //)
//        )
//        outp.byteEn := inp.byteEn
//        outp.src := outp.src.getZero
//
//        outp.burstCnt := outp.burstCnt.getZero
//        outp.burstFirst := False
//        outp.burstLast := False
//      }
//    )
//    mySdramCtrlIoctlHost.h2dBus <-< sdramInitFifo.io.pop
//    mySdramCtrlIoctlHost.d2hBus.ready := True
//  })
//  //--------
//  val fbAndDcacheArea = 
//    //new Area 
//    new ResetArea(
//      //myInnerResetCond,
//      //myMainResetCond,
//      //cartDownload,
//      //io.ioctl_download,
//      rose(io.ioctl_download),
//      cumulative=true//false//true//false//true
//    )
//  {
//    //--------
//    //--------
//    val myFbDcache = (
//      LcvBusCache(cfg=LcvBusCacheBusPairConfig(
//        mainCfg=cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg.mainCfg,
//        loBusCacheCfg=LcvBusCacheConfig(
//          kind=LcvCacheKind.D,
//          lineSizeBytes=64,
//          depthWords=(
//            //4 * 1024 / (4 * 2)
//            //256
//            //64
//            128
//          ).toInt,
//          numCpus=1,
//          lineWordMemRamStyleAltera=(
//            "no_rw_check, M10K"
//          ),
//          lineAttrsMemRamStyleAltera=(
//            "no_rw_check, MLAB"
//          ),
//        ),
//        hiBusCacheCfg=None,
//      ))
//    )
//    //val myFbDCacheLoBusClone = cloneOf(myFbDcache.io.loBus)
//    //myFbDcache.io.loBus
//
//    mySdramCtrlFbDcacheHost <-/< myFbDcache.io.hiBus
//    //--------
//    //val myNonFbDcache = (
//    //  LcvBusCache(cfg=cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg),
//    //)
//    //mySdramCtrlNonFbDcacheHost <-/< myNonFbDcache.io.hiBus
//    //--------
//    //--------
//    val myFbCtrl = LcvBusFramebufferCtrl(
//      cfg=(
//        cfg.myFbCfg
//      )
//    )
//    //vgaArea.lcvVgaCtrl.io.en := (
//    //  //True
//    //  //vgaArea.lcvVgaCtrl.io.push.valid
//    //  //||
//    //  RegNextWhen(
//    //    True,
//    //    cond=(
//    //      //vgaArea.lcvVgaCtrl.io.push.valid
//    //      myFbCtrl.io.pop.valid
//    //    ),
//    //    init=False,
//    //  )
//    //)
//    vgaCtrlArea.lcvVgaCtrl.io.push <-/< myFbCtrl.io.pop
//    //--------
//    def myFbArbiterHostIdxFbCtrl = 0
//    def myFbArbiterHostIdxCpu = 1
//    def limMyFbArbiterHostIdx = 2
//
//    val myFbArbiter = LcvBusArbiter(
//      cfg=LcvBusArbiterConfig(
//        busCfg=(
//          cfg.myFbCtrlMmapCfg.busCfg
//        ),
//        numHosts=limMyFbArbiterHostIdx,
//        kind=(
//          LcvBusArbiterKind.Priority
//          //LcvBusArbiterKind.RoundRobin
//        )
//      )
//    )
//    myFbArbiter.io.en := True
//    def myFbArbFbCtrlHost = (
//      myFbArbiter.io.hostVec(myFbArbiterHostIdxFbCtrl)
//    )
//    def myFbArbCpuHost = (
//      myFbArbiter.io.hostVec(myFbArbiterHostIdxCpu)
//    )
//
//    myFbArbFbCtrlHost <-/< myFbCtrl.io.bus
//    //--------
//    val myFbDeburster = LcvBusDeburster(
//      cfg=LcvBusDebursterConfig(
//        loBusCfg=(
//          //myFbDcache.cfg.loBusCfg
//          myFbArbiter.cfg.busCfg
//        )
//      )
//    )
//    myFbDeburster.io.loBus <-/< myFbArbiter.io.dev
//    myFbDcache.io.loBus <-/< myFbDeburster.io.hiBus
//    //--------
//  }
//  //object MyIrqState
//  //extends SpinalEnum(defaultEncoding=binaryOneHot) {
//  //  val
//  //    IDLE,
//  //    VBLANK
//  //    = newElement();
//  //}
//  case class MyDbgCpuDbusSearchElem(
//    addr: Long,
//    data: Option[Long],
//  ) {
//  }
//  val myCpuDbusWriteSearchArr = (
//    cfg.dbgUseLcvBusMem
//  ) generate {
//    val tempArr = new ArrayBuffer[MyDbgCpuDbusSearchElem]()
//    //--------
//    //tempArr += MyDbgCpuDbusSearchElem(addr=0x2ffffa4, data=0x4006c0)
//    //tempArr += MyDbgCpuDbusSearchElem(
//    //  addr=0x304e330,
//    //  data=Some(0x6e495f56)
//    //)
//    //tempArr += MyDbgCpuDbusSearchElem(
//    //  addr=0x304e330,
//    //  data=None,
//    //)
//    //--------
//    //tempArr += MyDbgCpuDbusSearchElem(
//    //  addr=0x476640,
//    //  data=Some(0x6e495f56)
//    //)
//    //tempArr += MyDbgCpuDbusSearchElem(
//    //  addr=0x476640,
//    //  data=None,
//    //)
//    //--------
//    tempArr += MyDbgCpuDbusSearchElem(
//      addr=0x368ce1c,
//      data=None,
//    )
//    tempArr += MyDbgCpuDbusSearchElem(
//      addr=0x368ce18,
//      data=None,
//    )
//    tempArr
//  }
//  val myCpuDbusWriteSearchCmpEqVec = (
//    cfg.dbgUseLcvBusMem
//  ) generate (
//    Vec.fill(myCpuDbusWriteSearchArr.size)(
//      Bool()
//    )
//  )
//  val cpuArea =
//    new ResetArea(
//      myMainResetCond,
//      cumulative=true//true//false//true
//    )
//    //new Area
//  {
//    //--------
//    val cpu = SnowHouseCpuWithoutRam(program=cfg.testProgram.program)
//
//    if (cfg.dbgUseLcvBusMem) {
//      for (idx <- 0 until myCpuDbusWriteSearchArr.size) {
//        val myAddr = myCpuDbusWriteSearchArr(idx).addr
//        val myData = myCpuDbusWriteSearchArr(idx).data
//
//        myCpuDbusWriteSearchCmpEqVec(idx) := (
//          cpu.io.lcvDbus.h2dBus.fire
//          && cpu.io.lcvDbus.h2dBus.isWrite
//          && cpu.io.lcvDbus.h2dBus.addr === myAddr
//          && (myData match {
//            case Some(data) => (
//              cpu.io.lcvDbus.h2dBus.data === data
//            )
//            case None => (
//              True
//            )
//          })
//        )
//        //when ((myCpuDbusWriteSearchCmpEqVec(idx))) {
//        //  report(Seq(
//        //    s"myCpuDbusWriteSearchCmpEqVec(${idx}): ",
//        //    cpu.io.lcvDbus.h2dBus.addr,
//        //    " ",
//        //    cpu.io.lcvDbus.h2dBus.data,
//        //  ))
//        //}
//      }
//    }
//    //--------
//    val irqCtrl = LcvBusIrqCtrl(
//      cfg=LcvBusIrqCtrlConfig(
//        busCfg=cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg.loBusCfg,
//        depth=2,
//      )
//    )
//    cpu.io.idsIraIrq.nextValid := irqCtrl.io.dstIrq.nextValid
//    irqCtrl.io.dstIrq.ready := cpu.io.idsIraIrq.ready
//
//    def myDoVblankIrq = otherVgaArea.myDoVblankIrq
//    irqCtrl.io.srcIrqVec(0) := myDoVblankIrq
//
//    val myTimerIrqOverflow = (
//      floor((cfg.clkRate / (1.0 kHz)).toDouble).toInt
//    )
//    val rTimerIrqCnt = (
//      Reg(UInt(log2Up(myTimerIrqOverflow + 1) + 1 bits))
//      init(0x0)
//    )
//    when (rTimerIrqCnt < myTimerIrqOverflow - 1) {
//      rTimerIrqCnt := rTimerIrqCnt + 1
//      irqCtrl.io.srcIrqVec(1) := False
//    } otherwise {
//      rTimerIrqCnt := 0x0
//      irqCtrl.io.srcIrqVec(1) := True
//    }
//    //val rMyIrqState = (
//    //  Reg(MyIrqState())
//    //  init(MyIrqState.IDLE)
//    //)
//    //cpu.io.idsIraIrq.nextValid
//
//    //val rIrqValid = Reg(Bool(), init=False)
//    //cpu.io.idsIraIrq.nextValid := (
//    //  rIrqValid
//    //  //RegNext(
//    //  //  cpu.io.idsIraIrq.nextValid,
//    //  //  init=cpu.io.idsIraIrq.nextValid.getZero
//    //  //)
//    //)
//    //vblankIrqFifo.io.pop.ready := False
//
//    //switch (rMyIrqState) {
//    //  is (MyIrqState.IDLE) {
//    //    when (
//    //      //rose(
//    //      //  RegNext(
//    //      //    lcvVgaCtrl.io.misc.vpipeS =/= LcvVgaState.visib,
//    //      //    init=False
//    //      //  )
//    //      //)
//    //      //rose(
//    //      //  RegNext(
//    //      //    //!io.vgaVisib
//    //      //    (
//    //      //      !io.vgaPhys.vsync
//    //      //      && !io.vgaPhys.hsync
//    //      //    ),
//    //      //    init=False
//    //      //  )
//    //      //)
//    //      vblankIrqFifo.io.pop.valid
//    //    ) {
//    //      vblankIrqFifo.io.pop.ready := True
//    //      rMyIrqState := MyIrqState.VBLANK
//    //      rIrqValid := True
//    //    }
//    //  }
//    //  is (MyIrqState.VBLANK) {
//    //    //cpu.io.idsIraIrq 
//    //    when (
//    //      //RegNext(
//    //      //  cpu.io.idsIraIrq.nextValid
//    //      //  init=False
//    //      //)
//    //      rIrqValid
//    //      && cpu.io.idsIraIrq.ready
//    //    ) {
//    //      rIrqValid := False
//    //      rMyIrqState := MyIrqState.IDLE
//    //    }
//    //  }
//    //}
//    //--------
//    val icache = LcvBusCache(cfg=cfg.cpuCfg.shCfg.subCfg.lcvIbusEtcCfg)
//    val myTempCpuLcvIbusD2hStm = cloneOf(cpu.io.lcvIbus.d2hBus)
//    val myHistCpuIbusD2hFire = History[Bool](
//      that=True,
//      length=2,
//      when=cpu.io.lcvIbus.d2hBus.fire,
//      init=False,
//    )
//    icache.io.loBus.h2dBus << cpu.io.lcvIbus.h2dBus
//    icache.io.loBus.d2hBus.translateInto(myTempCpuLcvIbusD2hStm)(
//      dataAssignment=(outp, inp) => {
//        outp := inp
//        val tempCond = (
//          inp.src
//          === (
//            RegNextWhen(
//              inp.src,
//              cond=icache.io.loBus.d2hBus.fire,
//            )
//            init(0x2)
//          )
//        )
//        val rState = Reg(Bool(), init=False)
//        when (
//          !myHistCpuIbusD2hFire.last
//          || (
//            tempCond
//            && !rState
//          )
//        ) {
//          outp.data := outp.data.getZero
//          when (inp.src === 0x3) {
//            rState := True
//          }
//        }
//      }
//    )
//    cpu.io.lcvIbus.d2hBus <-/< myTempCpuLcvIbusD2hStm
//    mySdramCtrlIcacheHost <-/< icache.io.hiBus
//    //--------
//    val myDbusSlicer = LcvBusSlicer(
//      cfg=LcvBusSlicerConfig(
//        mmapCfg=(
//          //cfg.myFbDbusSlicerMmapCfg
//          cfg.myDbusSlicerMmapCfg
//        ),
//        maxNumOutstandingTxns=(
//          // if this doesn't work, try increasing it.
//          // It has been reduced to shrink the size of the counter for fmax
//          // purposes
//          4
//        ),
//      )
//    )
//
//    val myFbCpuHostClone = cloneOf(fbAndDcacheArea.myFbArbCpuHost)
//
//    fbAndDcacheArea.myFbArbCpuHost <-/< myFbCpuHostClone
//
//    def mySlicedNonFbDcacheHost = myDbusSlicer.io.devVec(
//      //(cfg.myFbCtrlMmapCfg.optAddrSliceVal.get + 1) % 2
//      //0
//      cfg.myNonFbSdramAddrSliceVal
//    )
//    def mySlicedFbDcacheHost = myDbusSlicer.io.devVec(
//      //1
//      //cfg.myFbCtrlMmapCfg.optAddrSliceVal.get
//      //1
//      cfg.myFbAddrSliceVal
//    )
//    def mySlicedIoIrqCtrlHost = myDbusSlicer.io.devVec(
//      cfg.myIoIrqCtrlAddrSliceVal
//    )
//    def mySlicedIoDbgPrintHost = myDbusSlicer.io.devVec(
//      cfg.myIoDbgPrintAddrSliceVal
//    )
//
//    mySlicedFbDcacheHost.h2dBus.translateInto(myFbCpuHostClone.h2dBus)(
//      dataAssignment=(outp, inp) => {
//        outp.mainNonBurstInfo := inp.mainNonBurstInfo
//        outp.mainBurstInfo := outp.mainBurstInfo.getZero
//      }
//    )
//    myFbCpuHostClone.d2hBus.translateInto(mySlicedFbDcacheHost.d2hBus)(
//      dataAssignment=(outp, inp) => {
//        outp.mainNonBurstInfo := inp.mainNonBurstInfo
//      }
//    )
//
//    //val myFbCpuHostClone = cloneOf(myFbArbCpuHost)
//    //myDbusSlicer.io.host << cpu.io.lcvDbus
//    myDbusSlicer.io.host.h2dBus <-/< cpu.io.lcvDbus.h2dBus
//    cpu.io.lcvDbus.d2hBus << myDbusSlicer.io.host.d2hBus
//    //--------
//    // BEGIN: later `myNonFbDcache`
//    //0x44a90
//    val myNonFbDcache = (
//      LcvBusCache(cfg=cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg),
//    )
//    myNonFbDcache.io.loBus << mySlicedNonFbDcacheHost
//    mySdramCtrlNonFbDcacheHost <-/< myNonFbDcache.io.hiBus
//    // END: later `myNonFbDcache`
//    //--------
//    if (cfg.dbgExposeExtrasAtRegFileWrite) {
//      //val tempFuncStartAddr = 0x43984
//      io.regFileWriteActive := False
//      //io.cpuDbgInfo := io.cpuDbgInfo.getZero
//      io.cpuDbgInfo := cpu.io.dbgInfo
//      //io.cpuDbgInfo := cpu.io.dbgInfo
//      //io.regFileWriteAddr := 0x0
//      //io.regFileWriteData := 0x0
//      //io.laggingRegPcAtRegFileWrite := 0x0
//      when (
//        cpu.io.regFileWriteEnable
//        && !cpu.io.shouldIgnoreInstrAtRegFileWrite
//        //&& RegNextWhen(
//        //  True,
//        //  cond=(
//        //    cpu.io.laggingRegPcAtRegFileWrite
//        //    === tempFuncStartAddr
//        //  ),
//        //  init=False
//        //)
//      ) {
//        io.regFileWriteActive := True
//        //io.cpuDbgInfo := cpu.io.dbgInfo
//
//        //io.regFileWriteAddr := cpu.io.regFileWriteAddr
//        //io.regFileWriteData := cpu.io.regFileWriteData
//        //io.laggingRegPcAtRegFileWrite := (
//        //  cpu.io.laggingRegPcAtRegFileWrite.resize(
//        //    io.laggingRegPcAtRegFileWrite.getWidth
//        //  )
//        //)
//        //report(
//        //  Seq(
//        //    s"R_InitTextures(${tempFuncStartAddr}): debug: ",
//        //    "pc:", cpu.io.laggingRegPcAtRegFileWrite, " ",
//        //    "addr:", cpu.io.regFileWriteAddr,
//        //    "data:", cpu.io.regFileWriteData,
//        //  )
//        //)
//      }
//    }
//    //--------
//    //val myTempSlicedNonFbDcacheHost = cloneOf(mySlicedNonFbDcacheHost)
//    //myTempSlicedNonFbDcacheHost <-/< mySlicedNonFbDcacheHost
//
//    //val myNonFbDcacheH2dShiftedDataStmAdapter = {
//    //  val myCfg = myTempSlicedNonFbDcacheHost.cfg
//    //  LcvBusH2dShiftedDataEtcStreamAdapter(
//    //    cfg=LcvBusH2dShiftedDataEtcStreamAdapterConfig(
//    //      loBusCfg=LcvBusConfig(
//    //        mainCfg=myCfg.mainCfg.mkCopyWithoutByteEn(None),
//    //        cacheCfg=myCfg.cacheCfg
//    //      )
//    //    )
//    //  )
//    //}
//    //val myNonFbDcacheD2hShiftedDataStmAdapter = {
//    //  val myCfg = myTempSlicedNonFbDcacheHost.cfg
//    //  LcvBusD2hShiftedDataEtcStreamAdapter(
//    //    cfg=LcvBusD2hShiftedDataEtcStreamAdapterConfig(
//    //      busCfg=LcvBusConfig(
//    //        mainCfg=myCfg.mainCfg.mkCopyWithoutByteEn(None),
//    //        cacheCfg=myCfg.cacheCfg
//    //      )
//    //    )
//    //  )
//    //}
//
//    ////val myOtherSlicedNonFbDcacheHost = LcvBusIo(
//    ////  LcvBusConfig(
//    ////    mainCfg=myTempSlicedNonFbDcacheHost.cfg.mainCfg.mkCopyWithByteEn(
//    ////      optKeepByteSize=None
//    ////    ),
//    ////    cacheCfg=myTempSlicedNonFbDcacheHost.cfg.cacheCfg,
//    ////  )
//    ////)
//
//    //myNonFbDcacheH2dShiftedDataStmAdapter.io.loH2dBus << (
//    //  myTempSlicedNonFbDcacheHost.h2dBus
//    //)
//    ////myOtherSlicedNonFbDcacheHost.h2dBus << (
//    ////  myNonFbDcacheH2dShiftedDataStmAdapter.io.hiH2dBus
//    ////)
//
//    ////myNonFbDcacheD2hShiftedDataStmAdapter.io.loD2hBus << (
//    ////  myOtherSlicedNonFbDcacheHost.d2hBus
//    ////)
//    //
//    //myTempSlicedNonFbDcacheHost.d2hBus << (
//    //  myNonFbDcacheD2hShiftedDataStmAdapter.io.hiD2hBus
//    //)
//
//    //myNonFbDcacheH2dShiftedDataStmAdapter.io.loH2dBus.translateInto(
//    //  mySdramCtrlNonFbDcacheHost.h2dBus
//    //)(
//    //  dataAssignment=(outp, inp) => {
//    //    outp.mainNonBurstInfo := inp.mainNonBurstInfo
//    //    outp.mainBurstInfo := outp.mainBurstInfo.getZero
//    //  }
//    //)
//    //mySdramCtrlNonFbDcacheHost.d2hBus.translateInto(
//    //  myNonFbDcacheD2hShiftedDataStmAdapter.io.loD2hBus
//    //)(
//    //  dataAssignment=(outp, inp) => {
//    //    outp.mainNonBurstInfo := inp.mainNonBurstInfo
//    //  }
//    //)
//    ////mySdramCtrlNonFbDcacheHost <-/< mySlicedNonFbDcacheHost 
//    //--------
//
//    irqCtrl.io.bus <-/< mySlicedIoIrqCtrlHost 
//
//    val dbgPrint = MeltedMoonDbgPrint(cfg=cfg)
//    dbgPrint.io.bus <-/< mySlicedIoDbgPrintHost
//    //dbgPrint.io.outpChar.simPublic
//    io.outpChar := dbgPrint.io.outpChar
//
//    //mySlicedIoDbgPrintHost.h2dBus.ready := False
//    //mySlicedIoDbgPrintHost.d2hBus.valid := False
//    //mySlicedIoDbgPrintHost.d2hBus.payload := (
//    //  // this locks up the CPU! eek!
//    //  mySlicedIoDbgPrintHost.d2hBus.payload.getZero
//    //)
//  }
//  //vgaArea.lcvVgaCtrl.io.en := (
//  //  //True
//  //  //vgaArea.lcvVgaCtrl.io.push.valid
//  //  //||
//  //  RegNextWhen(
//  //    True,
//  //    cond=(
//  //      vgaArea.lcvVgaCtrl.io.push.valid
//  //      //myFbCtrl.io.pop.valid
//  //    ),
//  //    init=False,
//  //  )
//  //)
//}

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

	val cpuDbgInfo = (
	  cfg.dbgExposeExtrasAtRegFileWrite
	) generate (
	  out(SnowHouseDebugInfo(
	    cfg=cfg.cpuCfg.shCfg
	  ))
	)
	val regFileWriteActive = (
	  cfg.dbgExposeExtrasAtRegFileWrite
	) generate (
	  out(Bool())
	)

  val outpChar = out(Flow(UInt(8 bits)))
	//--------
}
case class MeltedMoonSimDut(
  cfg: MeltedMoonConfig
) extends Component {
  //--------
  val io = MeltedMoonSimDutIo(cfg=cfg)
  noIoPrefix()
  //--------
  //--------
  val meltedMoon = MeltedMoon(cfg=cfg)
  meltedMoon.io.pllLocked := RegNext(True, init=False)
  meltedMoon.io.softReset := RegNext(False, init=True)
  io.vgaPhys := meltedMoon.io.vgaPhys
  io.vgaPixelEn := meltedMoon.io.vgaPixelEn
  io.vgaVisib := meltedMoon.io.vgaVisib

  if (io.cpuDbgInfo != null) {
    io.cpuDbgInfo := meltedMoon.io.cpuDbgInfo
  }

  if (io.regFileWriteActive != null) {
    io.regFileWriteActive := meltedMoon.io.regFileWriteActive
  }

  io.outpChar := meltedMoon.io.outpChar
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
      //val depth = 1 << (16 - 4)
      val tempArr = new ArrayBuffer[BigInt]()
      tempArr ++= cfg.program.outpArr.view
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

    meltedMoon.io.ioctl.index := (
      meltedMoon.io.ioctl.index.getZero
      //Cat(
      //  sdramInitRam.io.rdDataPipe.valid
      //).asUInt.resize(meltedMoon.io.ioctl.index.getWidth)
    )
    meltedMoon.io.ioctl.index.allowOverride
    meltedMoon.io.ioctl.index.lsb := (
      sdramInitRam.io.rdDataPipe.valid
    )
    meltedMoon.io.ioctl.download := (
      sdramInitRam.io.rdDataPipe.valid
    )
    meltedMoon.io.ioctl.wr := sdramInitRam.io.rdDataPipe.valid
    meltedMoon.io.ioctl.dout := meltedMoon.io.ioctl.dout.getZero
    switch (sdramInitRam.io.rdDataPipe.addr(1 downto 1)) {
      for (idx <- 0 until 2) {
        is (idx) {
          def outp = meltedMoon.io.ioctl.dout
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
    sdramInitRam.io.rdDataPipe.ready := !meltedMoon.io.ioctl.myWait

    //meltedMoon.io.ioctl.dout := sdramInitRam.io.rdDataPipe.data

    meltedMoon.io.ioctl.addr := (
      sdramInitRam.io.rdDataPipe.addr.resize(
        meltedMoon.io.ioctl.addr.getWidth
      )
    )
  }
}
object MeltedMoonSim extends App {
  val cfg = MeltedMoonConfig(
    sdramCtrlUseAltddioOut=false,
    dbgExposeExtrasAtRegFileWrite=true,
    dbgUseLcvBusMem=true,
  )
  
  val numClkCycles: Long = 8192.toLong * 8 * 8 //* 8 * 8 * 8 * 8 * 8//2 //* 4//* 8 //* 4 * 8
  println(
    s"numClkCycles:${numClkCycles}"
  )
  val myCfg = Config.spinalExt(cfg.clkRate) 

  Config.simWithCfg(myCfg, withFstWave=true).compile({
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
        MeltedMoon(cfg=cfg)
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
      (((1 sec) / cfg.clkRate)) sec //ns //ms
    )
    //dut.meltedMoon.ioctlClkDomain.forkStimulus(
    //  (((1 sec) / cfg.demoCfg.clkRate)) sec //ns //ms
    //)
    //dut.meltedMoon.vgaClkDomain.forkStimulus(
    //  //40
    //  //(((1 sec) / cfg.demoCfg.vgaTimingInfo.pixelClk)) sec //ns //ms
    //  (((1 sec) / cfg.demoCfg.clkRate)) sec //ns //ms
    //)
    var toDbgPrint: String = ""
    var i: Long = 0.toLong
    //for (i <- 0 until numClkCycles)
    val pw = (
      //cfg.cpuCfg.programStr == "other_tests.ignore/melted_moon_doom.bin"
      true
    ) generate (
      new PrintWriter(new File(
        //s"melted_moon_doom-debug.txt"
        s"melted_moon-my_software_3d_renderer-debug.txt"
      ))
    )

    val mySavedRegsArr = new ArrayBuffer[Long]()
    for (idx <- 0 until 17) {
      mySavedRegsArr += 0.toLong
    }

    while (i < numClkCycles) {
      dut.clockDomain.waitSampling()
      def myOutpChar = dut.io.outpChar //dut.cpuArea.dbgPrint.io.outpChar
      def myRegFileWriteActive = dut.io.regFileWriteActive.toBoolean
      def myRegFileWriteAddr = (
        dut.io.cpuDbgInfo.regFileWriteAddr.toInt
      )
      def myRegFileWriteData = (
        dut.io.cpuDbgInfo.regFileWriteData.toLong
      )
      def myLaggingRegPcAtRegFileWrite = (
        dut.io.cpuDbgInfo.laggingRegPcAtRegFileWrite.toLong
      )
      //if (i == 0) {
      //  println(
      //    s"debug hexadecimal print: "
      //    + f"$myLaggingRegPcAtRegFileWrite%016x"
      //  )
      //}

      if (
        pw != null
        && myRegFileWriteActive
      ) {
        val tempCond = Array[Boolean](
          myRegFileWriteData != mySavedRegsArr(myRegFileWriteAddr),
          mySavedRegsArr.last != myLaggingRegPcAtRegFileWrite,
        )
        if (
          tempCond(0)
          || tempCond(1)
        ) {
          val myPcChngStr = (
            if (tempCond(1)) (
              "(y chng)"
            ) else (
              "(n chng)"
            )
          )
          pw.write(
            s"pc${myPcChngStr}:"
              + f"$myLaggingRegPcAtRegFileWrite%08x" + s"    "
            + s"wrAddr:" + f"$myRegFileWriteAddr" + s"    "
            + s"wrData:" + f"$myRegFileWriteData%08x" + s"    "//s"\n"
            //+ s"tempCond:(${tempCond(0)} ${tempCond(1)})" + s"\n"
            //+ s"\n"
          )
          pw.write(
            s"gprs:("
          )
          for (idx <- 0 until mySavedRegsArr.size - 1) {
            if (idx < 13) {
              pw.write(s"r${idx}")
            } else if (idx == 13) {
              pw.write(s"lr")
            } else if (idx == 14) {
              pw.write(s"fp")
            } else if (idx == 15) {
              pw.write(s"sp")
            } else {
              pw.write(s"eek! ${idx}")
            }

            pw.write(
              s"="
              + f"${mySavedRegsArr(idx)}%08x"
            )
            if (idx + 1 < mySavedRegsArr.size - 1) {
              pw.write(
                s" "
              )
            }
          }
          pw.write(
            s")"
            + s"\n"
          )
          //if (tempCond(0)) {
            mySavedRegsArr(myRegFileWriteAddr) = myRegFileWriteData
          //}

          mySavedRegsArr(16) = myLaggingRegPcAtRegFileWrite
        }
      }

      if (myOutpChar.valid.toBoolean) {
        if (myOutpChar.payload.toInt.toChar != '\n') {
          toDbgPrint = toDbgPrint + myOutpChar.payload.toInt.toChar
        } else {
          println(toDbgPrint)
          if (pw != null) {
            pw.write(toDbgPrint + s"\n")
            //if (
            //  toDbgPrint
            //  == "Error: R_InitTextures: Missing patch in texture COMP2"
            //) {
            //  pw.close()
            //  i = numClkCycles.toLong
            //}
          }
          toDbgPrint = ""
        }
      }
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
      i += 1
    }
    pw.close()
  }}
}
object MeltedMoonSimDutSim extends App {
  val cfg = MeltedMoonConfig(
    sdramCtrlUseAltddioOut=false,
    dbgUseLcvBusMem=false,
  )
  
  val numClkCycles = 8192 * 8 * 8 * 8 //* 8 // * 8//2 //* 4//* 8 //* 4 * 8
  val myCfg = Config.spinalExt(cfg.clkRate) 

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
      (((1 sec) / cfg.clkRate)) sec //ns //ms
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

object MeltedMoonDebugToVerilog extends App {
  val cfg = MeltedMoonConfig(
    sdramCtrlUseAltddioOut=false,
    dbgExposeExtrasAtRegFileWrite=true,
    dbgUseLcvBusMem=(
      //true
      false
    ),
  )
  Config.spinalExt(
    clkRate=cfg.clkRate,
    targetDirectory="hw/gen/meltedMoonDebug",
  ).generateVerilog{
    //MeltedMoon(cfg=cfg)
    MeltedMoonSimDut(cfg=cfg)
  }
}

object MeltedMoonToVerilog extends App {
  val cfg = MeltedMoonConfig(
    sdramCtrlUseAltddioOut=(
      true
    ),
  )
  Config.spinalExt(
    clkRate=cfg.clkRate,
    //targetDirectory="hw/gen/meltedMoonDebug",
    //resetKind=(
    //  SYNC
    //  //ASYNC
    //  //BOOT
    //),
  ).generateVerilog{
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

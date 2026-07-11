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


//static constexpr u32 ADDR_PRINT = 0x6000000ul;
//static constexpr u32 ADDR_EXIT = 0x6000004ul;
//static constexpr u32 ADDR_TIMER_USEC_LO = 0x6000000ul;
//static constexpr u32 ADDR_TIMER_USEC_HI = 0x6000004ul;
//static constexpr u32 ADDR_TIMER_SEC_LO = 0x6000008ul;
//static constexpr u32 ADDR_TIMER_SEC_HI = 0x600000cul;
//static constexpr u32 ADDR_UDIV64_INP_LEFT_LO = 0x6000010ul;
//static constexpr u32 ADDR_UDIV64_INP_LEFT_HI = 0x6000014ul;
//static constexpr u32 ADDR_UDIV64_INP_RIGHT_LO = 0x6000018ul;
//static constexpr u32 ADDR_UDIV64_INP_RIGHT_HI = 0x600001cul;
//static constexpr u32 ADDR_UDIV64_OUTP_QUOT_LO = 0x6000010ul;
//static constexpr u32 ADDR_UDIV64_OUTP_QUOT_HI = 0x6000014ul;
//static constexpr u32 ADDR_UDIV64_OUTP_REMA_LO = 0x6000018ul;
//static constexpr u32 ADDR_UDIV64_OUTP_REMA_HI = 0x600001cul;
//
//static constexpr u32 ADDR_IDIV64_INP_LEFT_LO = 0x6000020ul;
//static constexpr u32 ADDR_IDIV64_INP_LEFT_HI = 0x6000024ul;
//static constexpr u32 ADDR_IDIV64_INP_RIGHT_LO = 0x6000028ul;
//static constexpr u32 ADDR_IDIV64_INP_RIGHT_HI = 0x600002cul;
//static constexpr u32 ADDR_IDIV64_OUTP_QUOT_LO = 0x6000020ul;
//static constexpr u32 ADDR_IDIV64_OUTP_QUOT_HI = 0x6000024ul;
//static constexpr u32 ADDR_IDIV64_OUTP_REMA_LO = 0x6000028ul;
//static constexpr u32 ADDR_IDIV64_OUTP_REMA_HI = 0x600002cul;


case class MeltedMoonConfig(
  //sdramCfg: LcvBusSdramCtrlConfig=LcvBusSdramCtrlConfig(
  //  clkRate=100.0 MHz
  //),
  inSim: Boolean,
  sdramCtrlUseAltddioOut: Boolean=true,
  dbgExposeExtrasAtRegFileWrite: Boolean=false,
  dbgUseLcvBusMem: Boolean=false,
) {
  val simClkRate = (
    //75.0 MHz
    98.0 MHz
    //100.0 MHz
    //150.0 MHz 
    //50.0 MHz
    //75.0 MHz
  )
  val realClkRate = (
    //90.0 MHz
    simClkRate
  )
  val mainClkRate = (
    if (!inSim) (
      realClkRate
    ) else (
      simClkRate
    )
  )

  val simVgaClkRate = (
    simClkRate
  )
  val realVgaClkRate = (
    //84.0 MHz
    simClkRate
  )
  val mainVgaClkRate = (
    if (!inSim) (
      realVgaClkRate
    ) else (
      simVgaClkRate
    )
  )



  //val cpuCfg = SnowHouseCpuConfig(
  //  optFormal=false,
  //  targetAltera=true,
  //  //exposeModMemWordToIo=(
  //  //  //false
  //  //  true
  //  //),
  //  optMainAddrWidth=(
  //    Some(
  //      //25
  //      //26
  //      //27
  //      28
  //    )
  //  ),
  //  instrRamKind=0,
  //  programStr=(
  //    //"test/snowhousecpu-test-5.bin"
  //    //"test/snowhousecpu-framebuffer-demo.bin"
  //    "other_tests.ignore/melted_moon_doom.bin"
  //    //"other_tests.ignore/melted_moon_doom-enable_irqs.bin"
  //    //"other_tests.ignore/my_software_3d_renderer.bin"
  //    //"debug/snowhousecpu-framebuffer-demo-320x240.bin"
  //  ),
  //  exposeRegFileWriteDataToIo=dbgExposeExtrasAtRegFileWrite,
  //  exposeRegFileWriteAddrToIo=dbgExposeExtrasAtRegFileWrite,
  //  exposeRegFileWriteEnableToIo=dbgExposeExtrasAtRegFileWrite,
  //  dbgExposeExtrasAtRegFileWrite=dbgExposeExtrasAtRegFileWrite,
  //  optTwoCycleRegFileReads=(
  //    //true
  //    false
  //  ),
  //  regFileMemRamStyleAltera=(
  //    //"no_rw_check, logic"
  //    "no_rw_check, MLAB"
  //    //"no_rw_check, M10K"
  //    //"auto"
  //    //"M144K"
  //    //"no_rw_check"
  //  ),
  //  icacheLineWordMemRamStyleAltera=(
  //    //"no_rw_check, MLAB"
  //    "no_rw_check, M10K"
  //  ),
  //  icacheLineAttrsMemRamStyleAltera=(
  //    //"no_rw_check, MLAB"
  //    "no_rw_check, M10K"
  //    //"no_rw_check, logic"
  //    //"no_rw_check, MLAB"
  //  ),
  //  dcacheLineWordMemRamStyleAltera=(
  //    //"no_rw_check, MLAB"
  //    "no_rw_check, M10K"
  //    //"MLAB"
  //  ),
  //  dcacheLineAttrsMemRamStyleAltera=(
  //    //"no_rw_check, MLAB"
  //    "no_rw_check, M10K"
  //    //"no_rw_check, logic"
  //  ),
  //)
  ////val cfg = SnowHouseCpuConfig(
  ////  optFormal=(
  ////    //true
  ////    false
  ////  ),
  ////  exposeModMemWordToIo=true,
  ////)
  //val testProgram = SnowHouseCpuTestProgram(cfg=cpuCfg)
  ////SnowHouseCpuWithDualRam(program=testProgram.program)

  //def program = testProgram.program
  val cpuCfg = SnowHouseRiscv32imConfig(
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
      "other_tests.ignore/melted_moon_doom-riscv32-timedemo_3.bin"
      //"other_tests.ignore/melted_moon_doom-riscv32-no_timedemo.bin"
      //"other_tests.ignore/melted_moon_doom-enable_irqs.bin"
      //"other_tests.ignore/my_software_3d_renderer.bin"
      //"debug/snowhousecpu-framebuffer-demo-320x240.bin"
    ),
    //exposeRegFileWriteDataToIo=dbgExposeExtrasAtRegFileWrite,
    //exposeRegFileWriteAddrToIo=dbgExposeExtrasAtRegFileWrite,
    //exposeRegFileWriteEnableToIo=dbgExposeExtrasAtRegFileWrite,
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
    icacheDepthWords=(
      //8192  // numWays * (32 kiB) icache
      //4096  // numWays * (16 kiB) icache
      //2048 // numWays * (8 kiB) icache
      1024 // numWays * (4 kiB) icache
    ),
    icacheNumWays=(
      //2
      //8
      //2
      //4
      2
      //3
    ),
    dcacheDepthWords=(
      //8192 // numWays * (32 kiB) dcache
      //4096 // numWays * (16 kiB) dcache
      //2048 // numWays * (8 kiB) dcache
      1024 // numWays * (4 kiB) dcache
    ),
    dcacheNumWays=(
      //2
      2
      //4
      //8
      //3
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
    branchTgtBufSizeLog2=(
      log2Up(32)
    ),
    branchTgtBufNumWays=(
      1
      //2
      //4
    ),
  )

  val myDbusCfg = cpuCfg.shCfg.subCfg.lcvDbusEtcCfg.loBusCfg
  val rgbCfg=(
    //RgbConfig(rWidth=8, gWidth=8, bWidth=8)
    RgbConfig(rWidth=5, gWidth=5, bWidth=5)
  )
  val vgaTimingInfo = (
    LcvVgaTimingInfo(
      pixelClk=(
        24.5 MHz // for 98.0 MHz main clk (84 MHz VGA Ctrl clk)
        //28.0 MHz
      ),
      htiming=LcvVgaTimingHv(
        visib=(
          640
          //64 // debug
          //128
        ),
        front=8,
        sync=32,
        back=40
      ),
      vtiming=LcvVgaTimingHv(
        visib=(
          480
          //64 // debug
          //128
        ),
        front=2,
        sync=8,
        back=6
      ),
    ),
    //LcvVgaTimingInfoMap.map("640x480@70")
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

  val myDbgDbusSlicerAddrSliceHi = 26//27
  val myDbgDbusSlicerAddrSliceLo = 25//26
  //val myBackDbusSlicerAddrSliceHi = 27
  //val myBackDbusSlicerAddrSliceLo = 26
  //val myFrontDbusSlicerAddrSliceHi = 25
  //val myFrontDbusSlicerAddrSliceLo = 25

  def myNonFbSdramAddrDbgSliceVal = 0x0
  //val myFbOptAddrSliceVal = Some(1)
  //def myOtherDbgSliceVal = 0x1
  def myFbAddrDbgSliceVal = 0x1
  def myIoIrqCtrlAddrDbgSliceVal = 0x2
  def myIoNonIrqMmioAddrDbgSliceVal = 0x3

  //def myNonFbSdramAddrFrontSliceVal = 0x0
  ////val myFbOptAddrSliceVal = Some(1)
  //def myOtherFrontSliceVal = 0x1
  //def myFbAddrBackSliceVal = 0x0//0x1
  //def myIoIrqCtrlAddrBackSliceVal = 0x1//0x2
  //def myIoNonIrqMmioAddrBackSliceVal = 0x2//0x3

  val myDbgDbusSlicerMmapCfg = LcvBusMemMapConfig(
    busCfg=myDbusCfg,
    addrSliceHi=myDbgDbusSlicerAddrSliceHi,//25,
    addrSliceLo=myDbgDbusSlicerAddrSliceLo,//25,
    optAddrSliceVal=(
      // the framebuffer has bit 25 of the address asserted!
      //Some(1)
      //myFbOptAddrSliceVal
      None
    )
  )

  //val myFrontDbusSlicerMmapCfg = LcvBusMemMapConfig(
  //  busCfg=(
  //    myDbusCfg
  //    //LcvBusConfig(
  //    //  mainCfg=myDbusCfg.mainCfg.mkCopyWithAllowingBurst(),
  //    //  cacheCfg=myDbusCfg.cacheCfg,
  //    //)
  //  ),
  //  addrSliceHi=myFrontDbusSlicerAddrSliceHi,//25,
  //  addrSliceLo=myFrontDbusSlicerAddrSliceLo,//25,
  //  optAddrSliceVal=(
  //    // the framebuffer has bit 25 of the address asserted!
  //    //Some(1)
  //    //myFbOptAddrSliceVal
  //    None
  //  )
  //)
  //val myBackDbusSlicerMmapCfg = LcvBusMemMapConfig(
  //  busCfg=myDbusCfg,
  //  addrSliceHi=myBackDbusSlicerAddrSliceHi,//25,
  //  addrSliceLo=myBackDbusSlicerAddrSliceLo,//25,
  //  optAddrSliceVal=(
  //    // the framebuffer has bit 25 of the address asserted!
  //    //Some(1)
  //    //myFbOptAddrSliceVal
  //    None
  //  )
  //)

  val fbAddrSliceHi = 25//26 
    // memory-mapped IO registers start at (1 << (fbAddrSliceHi + 1))
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
      // the framebuffer has its "select" bit of the address asserted!
      //Some(1)
      Some(
        myFbAddrDbgSliceVal
        //myFbAddrBackSliceVal
      )
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
    clkRate=(
      mainClkRate
    ),
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


//case class MeltedMoonBramFbIo(
//  cfg: MeltedMoonConfig
//) extends Bundle {
//  val bus = slave(
//    LcvBusIo(cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg.loBusCfg)
//  )
//}
//case class MeltedMoonBramFb(
//  cfg: MeltedMoonConfig
//) extends Component {
//  val io = MeltedMoonBramFbIo(cfg=cfg)
//
//  val myPalMemDepth = 256
//  val myColIdxMemDepth = (
//    (cfg.myFbCfg.fbSize2d.x * cfg.myFbCfg.fbSize2d.y) >> 1//2
//  )
//
//  val myPalMem = Mem(
//    wordType=Rgb(cfg.myFbCfg.rgbCfg),
//    wordCount=myPalMemDepth,
//  )
//  val myColIdxMem = Mem(
//    wordType=UInt(log2Up(myPalMemDepth) bits),
//    wordCount=myColIdxMemDepth
//  )
//
//  //val myFbPalBram = {
//  //  val depth = 256
//  //  LcvBusMem(
//  //    cfg=LcvBusMemConfig(
//  //      busCfg=LcvBusConfig(
//  //        //cfg.sdramCtrlCfg.busCfg
//  //        mainCfg=LcvBusMainConfig(
//  //          dataWidth=32,
//  //          addrWidth=32,
//  //          allowBurst=true,
//  //          burstAlwaysMaxSize=true,
//  //          srcWidth=(
//  //            cfg.sdramCtrlCfg.busCfg.srcWidth
//  //          ),
//  //          haveByteEn=false,
//  //          keepByteSize=false,
//  //        ),
//  //      ),
//  //      depth=depth,
//  //      initBigInt=(
//  //        //Some(myMemInitBigInt)
//  //        None
//  //      ),
//  //    )
//  //  )
//  //}
//  //val myFbBram = {
//  //  val depth = (
//  //    ((cfg.myFbCfg.fbSize2d.x * cfg.myFbCfg.fbSize2d.y) >> 1)
//  //    //((cfg.myFbCfg.fbSize2d.x * cfg.myFbCfg.fbSize2d.y) >> 2)
//  //  )
//  //  //val myMemInitBigInt = {
//  //  //  //val depth = myMemDepth
//  //  //  
//  //  //  val tempArr = new ArrayBuffer[BigInt]()
//  //  //  //tempArr ++= program.view
//  //  //  while (tempArr.size < depth) {
//  //  //    tempArr += BigInt(0)
//  //  //  }
//  //  //  tempArr
//  //  //}
//  //  LcvBusMem(
//  //    cfg=LcvBusMemConfig(
//  //      busCfg=LcvBusConfig(
//  //        //cfg.sdramCtrlCfg.busCfg
//  //        mainCfg=LcvBusMainConfig(
//  //          dataWidth=32,
//  //          addrWidth=(
//  //            //27
//  //            32
//  //          ),
//  //          //burstSizeWidth=(
//  //          //  1
//  //          //),
//  //          //burstCntWidth=(
//  //          //  log2Up(((16 / 8) * burstLen /*64*/) / 4)
//  //          //    // the div by 4 is because of 32-bit `dataWidth`
//  //          //  //0
//  //          //  //1
//  //          //  //None
//  //          //  //Some(log2Up(64))
//  //          //),
//  //          //alwaysDoBurst=(
//  //          //  //true
//  //          //  false
//  //          //),
//  //          allowBurst=(
//  //            true
//  //            //busCfgAllowBurst
//  //          ),
//  //          burstAlwaysMaxSize=(
//  //            true
//  //          ),
//  //          srcWidth=(
//  //            //1
//  //            //None
//  //            //1
//  //            //srcWidth
//  //            cfg.sdramCtrlCfg.busCfg.srcWidth
//  //          ),
//  //          haveByteEn=false,
//  //          keepByteSize=false,
//  //        ),
//  //      ),
//  //      depth=depth,
//  //      initBigInt=(
//  //        //Some(myMemInitBigInt)
//  //        None
//  //      ),
//  //    )
//  //  )
//  //}
//}

case class MeltedMoonNonIrqMmioIo(
  cfg: MeltedMoonConfig,
) extends Bundle {
  val bus = slave(
    LcvBusIo(cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg.loBusCfg)
  )
  val outpChar = out(Flow(UInt(8 bits)))
}

case class MeltedMoonNonIrqMmio(
  cfg: MeltedMoonConfig,
) extends Component {
  //--------
  def busCfg = cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg.loBusCfg
  //--------
  val io = MeltedMoonNonIrqMmioIo(cfg=cfg)
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
      BUS_RD_NON_DIVMOD,
      BUS_WR_NON_DIVMOD,
      BUS_RD_DIVMOD,
      BUS_WR_DIVMOD_0,
      BUS_WR_DIVMOD_1,
      BUS_WR_DIVMOD_2,
      BUS_WR_DIVMOD_3,
      //BUS_WR_UDIV_RIGHT_HI,
      //BUS_WR_UMOD_RIGHT_HI,
      //BUS_WR_IDIV_RIGHT_HI,
      //BUS_WR_IMOD_RIGHT_HI
      BUS_RD_PALETTE,
      BUS_WR_PALETTE
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
  val myTimerUsecOverflow = (
    //floor((cfg.clkRate / (1.0 kHz)).toDouble).toInt
    floor((cfg.mainClkRate * (1.0 us)).toDouble).toInt
  )
  val myTimerSecOverflow = (
    //floor((cfg.clkRate / (1.0 kHz)).toDouble).toInt
    floor((cfg.mainClkRate * (1.0 sec)).toDouble).toInt
  )

  val rTimerUsecCnt = (
    Reg(UInt(log2Up(myTimerUsecOverflow + 1) + 1 bits))
    init(0x0)
  )
  val rTimerUsecOuterCnt = (
    Reg(cloneOf(io.bus.d2hBus.data), init=io.bus.d2hBus.data.getZero)
  )
  when (rTimerUsecCnt < myTimerUsecOverflow - 1) {
    rTimerUsecCnt := rTimerUsecCnt + 1
  } otherwise {
    rTimerUsecCnt := 0x0
    rTimerUsecOuterCnt := rTimerUsecOuterCnt + 1
  }

  val rTimerSecCnt = (
    Reg(UInt(log2Up(myTimerSecOverflow + 1) + 1 bits))
    init(0x0)
  )
  val rTimerSecOuterCnt = (
    Reg(cloneOf(io.bus.d2hBus.data), init=io.bus.d2hBus.data.getZero)
  )
  when (rTimerSecCnt < myTimerSecOverflow - 1) {
    rTimerSecCnt := rTimerSecCnt + 1
  } otherwise {
    rTimerSecCnt := 0x0
    rTimerSecOuterCnt := rTimerSecOuterCnt + 1
  }


  val fullAddrBase = 0x6000000

  def toRegIdx(fullAddr: Int): Int = (
    (fullAddr - fullAddrBase) >> 2
  )
  val regIdxDbgPrint = toRegIdx(0x6000000)
  val regIdxDbgExit = toRegIdx(0x6000004)

  val regIdxTimerUsecLo = toRegIdx(0x6000000)
  val regIdxTimerUsecHi = toRegIdx(0x6000004)
  val regIdxTimerSecLo = toRegIdx(0x6000008)
  val regIdxTimerSecHi = toRegIdx(0x600000c)

  val regIdxUdiv64InpLeftLo = toRegIdx(0x6000010)
  val regIdxUdiv64InpLeftHi = toRegIdx(0x6000014)
  val regIdxUdiv64InpRightLo = toRegIdx(0x6000018)
  val regIdxUdiv64InpRightHi = toRegIdx(0x600001c)
  val regIdxUdiv64OutpQuotLo = toRegIdx(0x6000010)
  val regIdxUdiv64OutpQuotHi = toRegIdx(0x6000014)
  val regIdxUdiv64OutpRemaLo = toRegIdx(0x6000018)
  val regIdxUdiv64OutpRemaHi = toRegIdx(0x600001c)

  val regIdxIdiv64InpLeftLo = toRegIdx(0x6000020)
  val regIdxIdiv64InpLeftHi = toRegIdx(0x6000024)
  val regIdxIdiv64InpRightLo = toRegIdx(0x6000028)
  val regIdxIdiv64InpRightHi = toRegIdx(0x600002c)
  val regIdxIdiv64OutpQuotLo = toRegIdx(0x6000020)
  val regIdxIdiv64OutpQuotHi = toRegIdx(0x6000024)
  val regIdxIdiv64OutpRemaLo = toRegIdx(0x6000028)
  val regIdxIdiv64OutpRemaHi = toRegIdx(0x600002c)

  //val regIdxPaletteStart     = toRegIdx(0x6000030)
  //val regIdxPaletteEnd       = regIdxPalette


  def toDivmodRegIdx(someDivmodRegIdx: Int): Int = (
    someDivmodRegIdx - regIdxUdiv64InpLeftLo
  )

  val numRegs = toRegIdx(0x600002c) + 1
  val numDivmodRegs = (
    regIdxIdiv64OutpRemaHi - regIdxUdiv64InpLeftLo + 1
    //toDivmodRegIdx(numRegs)
  )

  def myH2dBusRegIdx = (
    io.bus.h2dBus.addr(
      log2Up(numRegs) - 1 + 2 downto 2
    )
  )
  val rSavedDivmodH2dBusRegIdx = (
    Reg(
      //cloneOf(rSavedH2dPayload.addr(log2Up(numRegs) - 1 downto 2))
      UInt(log2Up(numRegs) + 2 - 2 bits)
    )
    init(0x0)
  )

  val divmod = LongDivMultiCycle(
    mainWidth=(cfg.cpuCfg.mainWidth * 2),
    denomWidth=(cfg.cpuCfg.mainWidth * 2),
    chunkWidth=1,//2,//1,//2,//1,//2,
    signedReset=0x0,
  )
  divmod.io.inp := divmod.io.inp.getZero

  val rUdiv64InpLeftLo = (
    Reg(UInt(cfg.cpuCfg.mainWidth bits))
    init(0x0)
  )
  val rUdiv64InpLeftHi = (
    Reg(UInt(cfg.cpuCfg.mainWidth bits))
    init(0x0)
  )
  val rUdiv64InpRightLo = (
    Reg(UInt(cfg.cpuCfg.mainWidth bits))
    init(0x0)
  )
  val rUdiv64InpRightHi = (
    Reg(UInt(cfg.cpuCfg.mainWidth bits))
    init(0x0)
  )
  val rUdiv64OutpQuotLo = (
    Reg(UInt(cfg.cpuCfg.mainWidth bits))
    init(0x0)
  )
  val rUdiv64OutpQuotHi = (
    Reg(UInt(cfg.cpuCfg.mainWidth bits))
    init(0x0)
  )
  val rUdiv64OutpRemaLo = (
    Reg(UInt(cfg.cpuCfg.mainWidth bits))
    init(0x0)
  )
  val rUdiv64OutpRemaHi = (
    Reg(UInt(cfg.cpuCfg.mainWidth bits))
    init(0x0)
  )

  val rIdiv64InpLeftLo = (
    Reg(UInt(cfg.cpuCfg.mainWidth bits))
    init(0x0)
  )
  val rIdiv64InpLeftHi = (
    Reg(UInt(cfg.cpuCfg.mainWidth bits))
    init(0x0)
  )
  val rIdiv64InpRightLo = (
    Reg(UInt(cfg.cpuCfg.mainWidth bits))
    init(0x0)
  )
  val rIdiv64InpRightHi = (
    Reg(UInt(cfg.cpuCfg.mainWidth bits))
    init(0x0)
  )

  val rIdiv64OutpQuotLo = (
    Reg(UInt(cfg.cpuCfg.mainWidth bits))
    init(0x0)
  )
  val rIdiv64OutpQuotHi = (
    Reg(UInt(cfg.cpuCfg.mainWidth bits))
    init(0x0)
  )
  val rIdiv64OutpRemaLo = (
    Reg(UInt(cfg.cpuCfg.mainWidth bits))
    init(0x0)
  )
  val rIdiv64OutpRemaHi = (
    Reg(UInt(cfg.cpuCfg.mainWidth bits))
    init(0x0)
  )

  switch (rState) {
    is (State.IDLE) {
      rSavedH2dPayload := io.bus.h2dBus.payload
      rSavedDivmodH2dBusRegIdx := (
        myH2dBusRegIdx - regIdxUdiv64InpLeftLo
      )

      switch (
        io.bus.h2dBus.valid
        ## io.bus.h2dBus.isWrite
        ## (myH2dBusRegIdx >= regIdxUdiv64InpLeftLo)
      ) {
        is (B"100") {
          io.bus.h2dBus.ready := True
          rState := State.BUS_RD_NON_DIVMOD
        }
        is (B"101") {
          io.bus.h2dBus.ready := True
          rState := State.BUS_RD_DIVMOD
        }
        is (B"110") {
          io.bus.h2dBus.ready := True
          rState := State.BUS_WR_NON_DIVMOD
        }
        is (B"111") {
          io.bus.h2dBus.ready := True
          rState := State.BUS_WR_DIVMOD_0
        }
        default {
        }
      }
    }
    is (State.BUS_RD_NON_DIVMOD) {
      io.bus.d2hBus.valid := True
      io.bus.d2hBus.src := rSavedH2dPayload.src
      when (!rSavedH2dPayload.addr(3)) {
        io.bus.d2hBus.data := rTimerUsecOuterCnt
      } otherwise {
        io.bus.d2hBus.data := rTimerSecOuterCnt
      }
      when (io.bus.d2hBus.fire) {
        rState := State.IDLE
      }
    }
    is (State.BUS_WR_NON_DIVMOD) {
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
    is (State.BUS_RD_DIVMOD) {
      io.bus.d2hBus.valid := True
      io.bus.d2hBus.src := rSavedH2dPayload.src
      switch (rSavedDivmodH2dBusRegIdx) {
        is (toDivmodRegIdx(regIdxUdiv64OutpQuotLo)) {
          io.bus.d2hBus.data := rUdiv64OutpQuotLo
        }
        is (toDivmodRegIdx(regIdxUdiv64OutpQuotHi)) {
          io.bus.d2hBus.data := rUdiv64OutpQuotHi
        }
        is (toDivmodRegIdx(regIdxUdiv64OutpRemaLo)) {
          io.bus.d2hBus.data := rUdiv64OutpRemaLo
        }
        is (toDivmodRegIdx(regIdxUdiv64OutpRemaHi)) {
          io.bus.d2hBus.data := rUdiv64OutpRemaHi
        }
        is (toDivmodRegIdx(regIdxIdiv64OutpQuotLo)) {
          io.bus.d2hBus.data := rIdiv64OutpQuotLo
        }
        is (toDivmodRegIdx(regIdxIdiv64OutpQuotHi)) {
          io.bus.d2hBus.data := rIdiv64OutpQuotHi
        }
        is (toDivmodRegIdx(regIdxIdiv64OutpRemaLo)) {
          io.bus.d2hBus.data := rIdiv64OutpRemaLo
        }
        is (toDivmodRegIdx(regIdxIdiv64OutpRemaHi)) {
          io.bus.d2hBus.data := rIdiv64OutpRemaHi
        }
        //for (myRegIdx <- 0 until numDivmodRegs) {
        //  is (myRegIdx) {
        //  }
        //}
      }
      when (io.bus.d2hBus.fire) {
        rState := State.IDLE
      }
    }
    is (State.BUS_WR_DIVMOD_0) {
      //io.outpChar.valid := io.bus.d2hBus.fire
      //io.outpChar.payload := rSavedH2dPayload.data(7 downto 0)

      io.bus.d2hBus.valid := True
      switch (rSavedDivmodH2dBusRegIdx) {
        is (toDivmodRegIdx(regIdxUdiv64InpLeftLo)) {
          rUdiv64InpLeftLo := rSavedH2dPayload.data
        }
        is (toDivmodRegIdx(regIdxUdiv64InpLeftHi)) {
          rUdiv64InpLeftHi := rSavedH2dPayload.data
        }
        is (toDivmodRegIdx(regIdxUdiv64InpRightLo)) {
          rUdiv64InpRightLo := rSavedH2dPayload.data
        }
        is (toDivmodRegIdx(regIdxUdiv64InpRightHi)) {
          rUdiv64InpRightHi := rSavedH2dPayload.data
          io.bus.d2hBus.valid := False
          rState := State.BUS_WR_DIVMOD_1
        }

        is (toDivmodRegIdx(regIdxIdiv64InpLeftLo)) {
          rIdiv64InpLeftLo := rSavedH2dPayload.data
        }
        is (toDivmodRegIdx(regIdxIdiv64InpLeftHi)) {
          rIdiv64InpLeftHi := rSavedH2dPayload.data
        }
        is (toDivmodRegIdx(regIdxIdiv64InpRightLo)) {
          rIdiv64InpRightLo := rSavedH2dPayload.data
        }
        is (toDivmodRegIdx(regIdxIdiv64InpRightHi)) {
          rIdiv64InpRightHi := rSavedH2dPayload.data
          io.bus.d2hBus.valid := False
          rState := State.BUS_WR_DIVMOD_1
        }
      }
      io.bus.d2hBus.src := rSavedH2dPayload.src
      when (io.bus.d2hBus.fire) {
        rState := State.IDLE
      }
    }
    is (State.BUS_WR_DIVMOD_1) {
      divmod.io.inp.valid := True
      when (!rSavedDivmodH2dBusRegIdx(2)) {
        divmod.io.inp.numer := (
          Cat(
            rUdiv64InpLeftHi,
            rUdiv64InpLeftLo,
          ).asUInt
        )
        divmod.io.inp.denom := (
          Cat(
            rUdiv64InpRightHi,
            rUdiv64InpRightLo,
          ).asUInt
        )
        divmod.io.inp.signed := False
      } otherwise {
        divmod.io.inp.numer := (
          Cat(
            rIdiv64InpLeftHi,
            rIdiv64InpLeftLo,
          ).asUInt
        )
        divmod.io.inp.denom := (
          Cat(
            rIdiv64InpRightHi,
            rIdiv64InpRightLo,
          ).asUInt
        )
        divmod.io.inp.signed := True
      }
      rState := State.BUS_WR_DIVMOD_2
    }
    is (State.BUS_WR_DIVMOD_2) {
      when (divmod.io.outp.ready) {
        when (!rSavedDivmodH2dBusRegIdx(2)) {
          rUdiv64OutpQuotLo := (
            divmod.io.outp.quot(31 downto 0)
          )
          rUdiv64OutpQuotHi := (
            divmod.io.outp.quot(63 downto 32)
          )
          rUdiv64OutpRemaLo := (
            divmod.io.outp.rema(31 downto 0)
          )
          rUdiv64OutpRemaHi := (
            divmod.io.outp.rema(63 downto 32)
          )
        } otherwise {
          rIdiv64OutpQuotLo := (
            divmod.io.outp.quot(31 downto 0)
          )
          rIdiv64OutpQuotHi := (
            divmod.io.outp.quot(63 downto 32)
          )
          rIdiv64OutpRemaLo := (
            divmod.io.outp.rema(31 downto 0)
          )
          rIdiv64OutpRemaHi := (
            divmod.io.outp.rema(63 downto 32)
          )
        }
        rState := State.BUS_WR_DIVMOD_3
      }
    }
    is (State.BUS_WR_DIVMOD_3) {
      io.bus.d2hBus.valid := True
      io.bus.d2hBus.src := rSavedH2dPayload.src
      when (io.bus.d2hBus.fire) {
        rState := State.IDLE
      }
    }
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

case class MeltedMoonDdramIo(
  cfg: MeltedMoonConfig,
) extends Bundle {
  val clk = out(Bool())
	val busy = in(Bool())
	val burstCnt = out(UInt(8 bits))
	val addr = out(UInt(29 bits))
	val dout = in(UInt(64 bits))
	val doutReady = in(Bool())
	val rd = out(Bool())
	val din = out(UInt(64 bits))
	val be = out(UInt(8 bits))
	val we = out(Bool())
}

case class MeltedMoonLcvBusToDdramBridgeIo(
  cfg: MeltedMoonConfig
) extends Bundle {
  val lcvBus = slave(LcvBusIo(cfg.sdramCtrlCfg.busCfg))
  val ddram = MeltedMoonDdramIo(cfg=cfg)
}


case class MeltedMoonLcvBusToDdramBridge(
  cfg: MeltedMoonConfig
) extends Component {
  val io = MeltedMoonLcvBusToDdramBridgeIo(cfg=cfg)
  def lcvBusCfg = io.lcvBus.cfg

  io.lcvBus.h2dBus.ready := False
  io.lcvBus.d2hBus.valid := False
  io.lcvBus.d2hBus.payload := io.lcvBus.d2hBus.payload.getZero

  io.ddram.clk := ClockDomain.current.readClockWire

  //io.ddram.burstCnt.setAsReg() init(0x0)
  io.ddram.addr.setAsReg() init(
    0x3 << io.ddram.addr.high - 4 + 1
  )
  io.ddram.rd.setAsReg() init(False)
  io.ddram.din.setAsReg() init(0x0)
  //io.ddram.be.setAsReg() init(0x0)
  io.ddram.be := U(io.ddram.be.getWidth bits, default -> True)
  io.ddram.we.setAsReg() init(False)

  val myDdramBurstCnt = (
    1 << (lcvBusCfg.burstCntWidth - 1)
  )
  val myFifoLatency = (
    //1
    0
  )
  val myFifoDepth = (
    if (myFifoLatency == 2) (
      // depth needs to be one *less* when `withAsyncRead==false`
      myDdramBurstCnt - 1
    ) else (
      myDdramBurstCnt
    )
  )
  println(
    s"debug: "
    + s"myDdramBurstCnt:${myDdramBurstCnt} "
    + s"myFifoDepth:${myFifoDepth} "
    + s"myFifoLatency:${myFifoLatency}"
  )

  io.ddram.burstCnt := (
    //myFifoDepth
    myDdramBurstCnt
  )

  val myDdramAddrRange = (
    io.ddram.addr.high - 4 - 3
    downto 0
  )
  val myLcvAddrRange = (
    io.ddram.addr.high - 4 - 3 + 3
    downto 3
  )

  val rSavedLcvH2dPayload = {
    val temp = Reg(cloneOf(io.lcvBus.h2dBus.payload))
    temp.init(temp.getZero)
    temp
  }


  val rdFifo = StreamFifo(
    dataType=cloneOf(io.ddram.dout),
    depth=myFifoDepth,
    latency=myFifoLatency,
    forFMax=true,
  )
  val wrFifo = StreamFifo(
    dataType=cloneOf(io.ddram.dout),
    depth=myFifoDepth,
    latency=myFifoLatency,
    forFMax=true,
  )

  //rdFifo.io.push.valid := False
  //rdFifo.io.push.payload := rdFifo.io.push.payload.getZero
  //rdFifo.io.pop.ready := False

  wrFifo.io.push.valid := False
  wrFifo.io.push.payload := wrFifo.io.push.payload.getZero
  wrFifo.io.pop.ready := False

  rdFifo.io.push.valid := False//io.ddram.doutReady
  rdFifo.io.push.payload := rdFifo.io.push.payload.getZero//io.ddram.dout
  rdFifo.io.pop.ready := False


  val rRdLcvBurstCnt = (
    Reg(UInt(lcvBusCfg.burstCntWidth bits))
    init(lcvBusCfg.maxBurstSizeMinus1)
  )
  val rWrLcvBurstCnt = (
    Reg(UInt(lcvBusCfg.burstCntWidth bits))
    init(lcvBusCfg.maxBurstSizeMinus1)
  )
  val rWrDdramBurstCnt = (
    Reg(UInt(log2Up(myDdramBurstCnt) bits))
    init(myDdramBurstCnt - 1)
  )

  val stateWidth = 3
  def STATE_IDLE = U(s"${stateWidth}'b000")
  def STATE_RD_BURST_START = U(s"${stateWidth}'b001")
  def STATE_RD_BURST_PUSH_FIFO = U(s"${stateWidth}'b010")
  def STATE_RD_BURST_POP_FIFO = U(s"${stateWidth}'b011")
  def STATE_WR_BURST_PUSH_FIFO = U(s"${stateWidth}'b100")
  def STATE_WR_BURST_POP_FIFO = U(s"${stateWidth}'b101")
  def STATE_WR_BURST_LCV_D2H_RESP = U(s"${stateWidth}'b110")

  val rState = Reg(UInt(stateWidth bits), init=STATE_IDLE)

  io.ddram.rd := False
  io.ddram.we := False

  io.lcvBus.d2hBus.src.allowOverride
  io.lcvBus.d2hBus.src := rSavedLcvH2dPayload.src

  val rDidFirstDdramRd = Reg(Bool(), init=False)

  switch (rState) {
    is (STATE_IDLE) {
      rSavedLcvH2dPayload := io.lcvBus.h2dBus.payload
      switch (
        io.lcvBus.h2dBus.valid
        ## io.lcvBus.h2dBus.isWrite
      ) {
        is (B"10") {
          rState := STATE_RD_BURST_START
        }
        is (B"11") {
          rState := STATE_WR_BURST_PUSH_FIFO
        }
        default {
        }
      }
    }
    is (STATE_RD_BURST_START) {
      when (
        rose(rState === STATE_RD_BURST_START)
      ) {
        io.lcvBus.h2dBus.ready := True
        io.ddram.rd := True
      }
      //io.ddram.rd := True
      io.ddram.addr(myDdramAddrRange) := (
        rSavedLcvH2dPayload.addr(myLcvAddrRange)
      )
      when (
        io.ddram.rd
        && !io.ddram.busy
        && !rDidFirstDdramRd
        //&& rdFifo.io.occupancy === myDdramBurstCnt - 1
        //&& rdFifo.io.push.fire
      ) {
        io.ddram.rd := False
        rDidFirstDdramRd := True
        //rState := STATE_RD_BURST_PUSH_FIFO
      }
      rdFifo.io.push.valid := io.ddram.doutReady
      rdFifo.io.push.payload := io.ddram.dout
      when (rdFifo.io.push.fire) {
        rState := STATE_RD_BURST_PUSH_FIFO
      }
    }
    is (STATE_RD_BURST_PUSH_FIFO) {
      rDidFirstDdramRd := False
      when (!rdFifo.io.availability.orR) {
        rState := STATE_RD_BURST_POP_FIFO
      }
      rdFifo.io.push.valid := io.ddram.doutReady
      rdFifo.io.push.payload := io.ddram.dout
    }
    is (STATE_RD_BURST_POP_FIFO) {
      io.lcvBus.d2hBus.burstFirst := (
        rRdLcvBurstCnt === lcvBusCfg.maxBurstSizeMinus1
      )
      io.lcvBus.d2hBus.burstLast := (
        !rRdLcvBurstCnt.orR
      )
      io.lcvBus.d2hBus.burstCnt := rRdLcvBurstCnt

      //val (myRdReptStm, myRdReptCnt) = (
      //  // we only want to duplicate *once*, so we use `.repeat(2)`
      //  rdFifo.io.pop.repeat(2)
      //)
      val myRdPopStm = cloneOf(rdFifo.io.pop)
      myRdPopStm.valid := rdFifo.io.pop.valid //True
      myRdPopStm.payload := rdFifo.io.pop.payload

      myRdPopStm.translateInto(
        io.lcvBus.d2hBus
      )(
        dataAssignment=(outp, inp) => {
          //outp.src := rSavedLcvH2dPayload.src
          switch (
            //myRdReptCnt
            rRdLcvBurstCnt.lsb
          ) {
            is (False) {
              outp.data := inp(31 downto 0)
            }
            is (True) {
              outp.data := inp(63 downto 32)
            }
          }
        }
      )
      when (
        io.lcvBus.d2hBus.fire
        && !rRdLcvBurstCnt.lsb
      ) {
        rdFifo.io.pop.ready := True
      }
      when (io.lcvBus.d2hBus.fire) {
        rRdLcvBurstCnt := rRdLcvBurstCnt - 1
      }
      when (
        io.lcvBus.d2hBus.fire 
        && !rRdLcvBurstCnt.orR
      ) {
        rState := STATE_IDLE
      }
    }
    is (STATE_WR_BURST_PUSH_FIFO) {
      io.lcvBus.h2dBus.ready := True

      wrFifo.io.push.payload := (
        RegNext(
          wrFifo.io.push.payload,
          init=wrFifo.io.push.payload.getZero
        )
      )
      when (io.lcvBus.h2dBus.fire) {
        rWrLcvBurstCnt := rWrLcvBurstCnt - 1
      }
      when (rWrLcvBurstCnt.lsb) {
        wrFifo.io.push.payload(63 downto 32) := io.lcvBus.h2dBus.data
      } otherwise {
        wrFifo.io.push.payload(31 downto 0) := io.lcvBus.h2dBus.data
      }

      when (fell(rWrLcvBurstCnt.lsb)) {
        wrFifo.io.push.valid := True
      }
      when (
        io.lcvBus.h2dBus.fire
        && !rWrLcvBurstCnt.orR
      ) {
        rState := STATE_WR_BURST_POP_FIFO
      }
    }
    is (STATE_WR_BURST_POP_FIFO) {
      io.ddram.we := True
      io.ddram.addr(myDdramAddrRange) := (
        rSavedLcvH2dPayload.addr(myLcvAddrRange)
      )
      when (wrFifo.io.pop.fire) {
        rWrDdramBurstCnt := rWrDdramBurstCnt - 1
      }
      when (
        //io.ddram.we && !io.ddram.busy
        ////&& !wrFifo.io.occupancy.orR
        ////&& wrFifo.io.occupancy === 1
        ////////&& RegNext(wrFifo.io.pop.fire)
        //&& 
        wrFifo.io.pop.fire
        && !rWrDdramBurstCnt.orR
        //&& !wrFifo.io.pop.valid
      ) {
        io.ddram.we := False
        rState := STATE_WR_BURST_LCV_D2H_RESP
      }
      io.ddram.din := wrFifo.io.pop.payload
      wrFifo.io.pop.ready := (
        io.ddram.we && !io.ddram.busy
      )
    }
    is (STATE_WR_BURST_LCV_D2H_RESP) {
      io.lcvBus.d2hBus.valid := True
      io.lcvBus.d2hBus.burstFirst := True
      io.lcvBus.d2hBus.burstLast := True

      when (io.lcvBus.d2hBus.ready) {
        rState := STATE_IDLE
      }
    }
  }

  //val stateWidth = 3
  //def STATE_IDLE = U(s"${stateWidth}'b000")
  //def STATE_RD_BURST_PUSH_FIFO = U(s"${stateWidth}'b001")
  //def STATE_RD_BURST_POP_FIFO = U(s"${stateWidth}'b010")
  //def STATE_WR_BURST_PUSH_FIFO = U(s"${stateWidth}'b011")
  //def STATE_WR_BURST_POP_FIFO = U(s"${stateWidth}'b100")

  //val rState = Reg(UInt(stateWidth bits), init=STATE_IDLE)


  //val numWrBurst
}

//case class MeltedMoonLcvBusToDdramBridge(
//  cfg: MeltedMoonConfig
//) extends Component {
//  val io = MeltedMoonLcvBusToDdramBridgeIo(cfg=cfg)
//  def lcvBusCfg = io.lcvBus.cfg
//
//  io.lcvBus.h2dBus.ready := False
//  io.lcvBus.d2hBus.valid := False
//  io.lcvBus.d2hBus.payload := io.lcvBus.d2hBus.payload.getZero
//
//  io.ddram.clk := ClockDomain.current.readClockWire
//
//  //io.ddram.burstCnt.setAsReg() init(0x0)
//  io.ddram.addr.setAsReg() init(
//    0x3 << io.ddram.addr.high - 4 + 1
//  )
//  io.ddram.rd.setAsReg() init(False)
//  io.ddram.din.setAsReg() init(0x0)
//  //io.ddram.be.setAsReg() init(0x0)
//  io.ddram.be := U(io.ddram.be.getWidth bits, default -> True)
//  io.ddram.we.setAsReg() init(False)
//
//
//  //when (!io.ddram.busy) {
//  //  io.ddram.burstCnt := 0x0
//  //  io.ddram.rd := False
//  //  io.ddram.we := False
//  //}
//
//  //io.ddram.addr := (
//  //  RegNext(
//  //    io.ddram.addr,
//  //    init=io.ddram.addr.getZero
//  //  )
//  //)
//  //io.ddram.addr.allowOverride
//  //io.ddram.addr(
//  //  io.ddram.addr.high downto io.ddram.addr.high - 4 + 1
//  //) := (
//  //  0x3
//  //)
//  val myDdramAddrRange = (
//    io.ddram.addr.high - 4
//    downto 0
//  )
//  val myLcvAddrRange = (
//    io.ddram.addr.high - 4 + 3
//    downto 3
//  )
//
//  val rSavedLcvH2dPayload = {
//    val temp = Reg(cloneOf(io.lcvBus.h2dBus.payload))
//    temp.init(temp.getZero)
//    temp
//  }
//
//  case class WrFifoElem(
//    isLcvBus: Boolean,
//  ) extends Bundle {
//    val data = UInt(
//      (
//        if (isLcvBus) (
//          io.lcvBus.h2dBus.data.getWidth
//        ) else (
//          io.ddram.din.getWidth
//        )
//      ) bits
//    )
//    //val byteEn = UInt(
//    //  (
//    //    if (isLcvBus) (
//    //      io.lcvBus.h2dBus.byteEn.getWidth
//    //    ) else (
//    //      io.ddram.be.getWidth
//    //    )
//    //  ) bits
//    //)
//  }
//
//  //val myLcvFifoDepth = (
//  //  lcvBusCfg.burstCntMaxNumBytes >> 2
//  //)
//  //val myWrFifoDepth = (
//  //  //lcvBusCfg.burstCntMaxNumBytes >> 2//3
//  //  1 << lcvBusCfg.burstCntWidth
//  //)
//  val myDdramBurstCnt = (
//    //lcvBusCfg.burstCntMaxNumBytes >> 2//3
//    //myDdramFifoDepth
//    //1 << lcvBusCfg.burstCntWidth
//    //myWrFifoDepth
//    //1 << lcvBusCfg.burstCntWidth
//    1 << (lcvBusCfg.burstCntWidth - 1)
//  )
//  //val myRdDdramBurstCnt = (
//  //  1 << (lcvBusCfg.burstCntWidth - 1)
//  //)
//  //println(
//  //  s"debug: "
//  //  + s"myWrDdramBurstCnt:${myWrDdramBurstCnt} "
//  //  + s"myRdDdramBurstCnt:${myRdDdramBurstCnt}"
//  //)
//  //val myRdLcvBurstCnt = (
//  //  1 << lcvBusCfg.burstCntWidth
//  //)
//
//  //val rdLcvFifo = StreamFifo(
//  //  dataType=cloneOf(io.lcvBus.d2hBus.data),
//  //  depth=(
//  //    //myLcvFifoDepth
//  //    myRdLcvBurstCnt
//  //  ),
//  //  latency=2,
//  //  forFMax=true,
//  //)
//
//  //val myFifoLatency = (
//  //  1
//  //)
//
//  //val myFifoDepth = (
//  //  if (myFifoLatency == 2) (
//  //    // depth needs to be one *less* when `withAsyncRead==false`
//  //    myDdramBurstCnt - 1
//  //  ) else (
//  //    myDdramBurstCnt
//  //  )
//  //)
//  val rdDdramFifo = StreamFifo(
//    dataType=cloneOf(io.ddram.dout),
//    depth=(
//      //myDdramFifoDepth
//      //myRdDdramBurstCnt
//      myDdramBurstCnt
//    ),
//    latency=(
//      //2
//      //1
//      0
//    ),
//    forFMax=true,
//  )
//  rdDdramFifo.io.push.valid := False
//  rdDdramFifo.io.push.payload := rdDdramFifo.io.push.payload.getZero
//  //rdDdramFifo.io.pop.ready := False
//
//  //val wrLcvFifo = StreamFifo(
//  //  dataType=(
//  //    //cloneOf(io.lcvBus.h2dBus.payload)
//  //    WrFifoElem(isLcvBus=true)
//  //  ),
//  //  depth=myLcvFifoDepth,
//  //  latency=2,
//  //  forFMax=true,
//  //)
//
//  val wrDdramFifo = StreamFifo(
//    dataType=(
//      //cloneOf(io.lcvBus.h2dBus.payload)
//      WrFifoElem(isLcvBus=false)
//    ),
//    depth=(
//      //myWrFifoDepth
//      myDdramBurstCnt
//    ),
//    latency=(
//      //2
//      //1
//      0
//    ),
//    forFMax=true,
//  )
//  wrDdramFifo.io.push.valid := False
//  wrDdramFifo.io.push.payload := wrDdramFifo.io.push.payload.getZero
//  wrDdramFifo.io.pop.ready := False
//
//  //val rRdTempCnt = (
//  //  Reg(UInt(1 bits), init=U(0))
//  //)
//  //val rWrTempCnt = (
//  //  Reg(UInt(1 bits), init=U(0))
//  //)
//
//  //val rMyWrDdramFifoElem = (
//  //)
//
//  //object State
//  //extends SpinalEnum(defaultEncoding=binarySequential) {
//  //  val
//  //    IDLE,                   // 0b000
//  //    //RD_NON_BURST,           // 0b001
//  //    //WR_NON_BURST,           // 0b010
//  //    RD_BURST_PUSH_FIFO,     // 0b001 // 0b011
//  //    RD_BURST_POP_FIFO,      // 0b010 // 0b100
//  //    WR_BURST_PUSH_FIFO,     // 0b011 // 0b101
//  //    WR_BURST_POP_FIFO       // 0b100 // 0b110
//  //    = newElement()
//  //}
//  val stateWidth = 3
//  val STATE_IDLE = 0x0
//  val STATE_RD_BURST_PUSH_FIFO = 0x1     // 0b001 // 0b011
//  val STATE_RD_BURST_POP_FIFO = 0x2      // 0b010 // 0b100
//  val STATE_WR_BURST_PUSH_FIFO = 0x3     // 0b011 // 0b101
//  val STATE_WR_BURST_POP_FIFO = 0x4       // 0b100 // 0b110
//  val rState = (
//    //Reg(State()) init(State.IDLE)
//    Reg(UInt(stateWidth bits))
//    init(0x0)
//  )
//
//  val rSeenFinalRdDdramBurstCnt = Reg(Bool(), init=False)
//  val rRdDdramBurstCnt = (
//    Reg(UInt(lcvBusCfg.burstCntWidth - 1 bits))
//    init(
//      //(lcvBusCfg.maxBurstSizeMinus1 + 1) >> 1
//      myDdramBurstCnt - 1
//    )
//  )
//  val rRdLcvBurstCnt = (
//    Reg(UInt(lcvBusCfg.burstCntWidth bits))
//    init (
//      //0
//      lcvBusCfg.maxBurstSizeMinus1
//    )
//  )
//  when (
//    //!io.ddram.busy
//    //&& 
//    rState === STATE_RD_BURST_POP_FIFO
//    && io.lcvBus.d2hBus.fire
//  ) {
//    rRdLcvBurstCnt := rRdLcvBurstCnt - 1
//  }
//  when (
//    !rRdLcvBurstCnt.orR
//  ) {
//    io.lcvBus.d2hBus.burstLast := True
//  }
//  //switch (
//  //  (rState === STATE_RD_BURST_POP_FIFO)
//  //  ## io.lcvBus.d2hBus.fire
//  //  ## rRdLcvBurstCnt.orR
//  //) {
//  //  is (M"1-0") {
//  //    io.lcvBus.d2hBus.burstLast := True
//  //  }
//  //  is (B"111") {
//  //    rRdLcvBurstCnt := rRdLcvBurstCnt - 1
//  //  }
//  //  //is (M"10-") {
//  //  //}
//  //  default {
//  //    rRdLcvBurstCnt := lcvBusCfg.maxBurstSizeMinus1
//  //  }
//  //}
//  val rSeenFirstRdLcvD2hFire = Reg(Bool(), init=False)
//  switch (
//    (
//      //!io.ddram.busy
//      //&& 
//      rState === STATE_RD_BURST_POP_FIFO
//    )
//    ## rSeenFirstRdLcvD2hFire
//  ) {
//    is (B"10") {
//      //io.lcvBus.d2hBus.valid := True
//      //io.lcvBus.d2hBus.src := rSavedLcvH2dPayload.src
//      io.lcvBus.d2hBus.burstFirst := True
//
//      when (io.lcvBus.d2hBus.fire) {
//        rSeenFirstRdLcvD2hFire := True
//      }
//    }
//    is (B"11") {
//    }
//    default {
//      rSeenFirstRdLcvD2hFire := False
//    }
//    //is (M"0-") {
//    //}
//  }
//
//  val rSeenWrLcvD2hFire = Reg(Bool(), init=False)
//
//  switch (
//    //!io.ddram.busy
//    //## 
//    (rState === STATE_WR_BURST_POP_FIFO)
//    ## rSeenWrLcvD2hFire
//  ) {
//    is (B"10") {
//      io.lcvBus.d2hBus.valid := True
//      io.lcvBus.d2hBus.src := rSavedLcvH2dPayload.src
//      io.lcvBus.d2hBus.burstFirst := True
//      io.lcvBus.d2hBus.burstLast := True
//
//      when (io.lcvBus.d2hBus.ready) {
//        rSeenWrLcvD2hFire := True
//      }
//    }
//    is (B"11") {
//    }
//    default {
//      rSeenWrLcvD2hFire := False
//    }
//    //is (M"0-") {
//    //}
//  }
//  val (myRdReptStm, myRdReptCnt) = (
//    // we only want to duplicate *once*, so we use `.repeat(2)`
//    rdDdramFifo.io.pop.repeat(2)
//  )
//  myRdReptStm.ready := False
//  //val myBugCond = KeepAttribute(Bool())
//  //myBugCond := (
//  //  //False
//  //  rState === STATE_IDLE
//  //  && rRdLcvBurstCnt === 0x0
//  //)
//  //when (myBugCond) {
//  //  report(
//  //    "eek!"
//  //  )
//  //}
//  val rDidFirstDdramRd = Reg(Bool(), init=False)
//  val rDidFirstDdramWe = Reg(Bool(), init=False)
//
//  val rMyDbgCondIoDdramVec = Vec.fill(2)(
//    Reg(Bool(), init=False)
//  )
//  //val myDbgHistIoDdramVec = (
//  //  //val depth = 
//  //  rMyDbgCondIoDdramVec = Vec(
//  //    rMyDbgCondIoDdramVec.map(item => {
//  //    })
//  //  )
//  //  //Vec(
//  //  //  History[MeltedMoonDdramIo](
//  //  //    that=io.ddram,
//  //  //    length=depth,
//  //  //    when=io.ddram.rd,
//  //  //    init=io.ddram.getZero,
//  //  //  ),
//  //  //  History[MeltedMoonDdramIo](
//  //  //    that=io.ddram,
//  //  //    length=depth,
//  //  //    when=io.ddram.we,
//  //  //    init=io.ddram.getZero,
//  //  //  )
//  //  //)
//  //)
//
//  //switch (
//  //  Cat(
//  //    io.ddram.busy,
//  //    rState,//.asBits,
//  //  )
//  //  //rState//.asBits,
//  //) {
//  //  is (
//  //    MaskedLiteral(
//  //      //"-" + ("0" * STATE_IDLE.asBits.getWidth)
//  //      "-" + ("0" * stateWidth)
//  //    )
//  //    //STATE_IDLE
//  //  ) {
//  //    rSavedLcvH2dPayload := io.lcvBus.h2dBus.payload
//  //    switch (
//  //      io.lcvBus.h2dBus.valid
//  //      //## io.lcvBus.h2dBus.burstFirst
//  //      ## io.lcvBus.h2dBus.isWrite
//  //      //## rdLcvFifo.io.occupancy == 
//  //      //## wrDdramFifo.io.occupancy.orR
//  //    ) {
//  //      //is (B"100") {
//  //      //  //io.lcvBus.h2dBus.ready := True
//  //      //  rState := STATE_RD_NON_BURST
//  //      //}
//  //      //is (B"101") {
//  //      //  //io.lcvBus.h2dBus.ready := True
//  //      //  rState := STATE_WR_NON_BURST
//  //      //}
//  //      is (
//  //        //B"110"
//  //        B"10"
//  //      ) {
//  //        //io.lcvBus.h2dBus.ready := True
//  //        rState := STATE_RD_BURST_PUSH_FIFO
//  //      }
//  //      is (
//  //        //B"111"
//  //        B"11"
//  //      ) {
//  //        rState := STATE_WR_BURST_PUSH_FIFO
//  //      }
//  //      default {
//  //      }
//  //    }
//  //  }
//  //  //is (Cat(False, STATE_RD_NON_BURST)) {
//  //  //}
//  //  //is (Cat(False, STATE_WR_NON_BURST)) {
//  //  //}
//  //  is (
//  //    //Cat(False, STATE_RD_BURST_PUSH_FIFO.asBits)
//  //    B(s"${stateWidth + 1}'d${STATE_RD_BURST_PUSH_FIFO}")
//  //    //STATE_RD_BURST_PUSH_FIFO
//  //  ) {
//  //    io.lcvBus.h2dBus.ready := True
//
//  //    io.ddram.addr(myDdramAddrRange) := (
//  //      rSavedLcvH2dPayload.addr(myLcvAddrRange)
//  //    )
//  //    //io.ddram.burstCnt := myRdDdramBurstCnt
//  //    when (
//  //      //!io.ddram.busy
//  //      //&& rose(rState === STATE_RD_BURST_PUSH_FIFO)
//  //      !rDidFirstDdramRd
//  //    ) {
//  //      io.ddram.rd := True
//  //      rDidFirstDdramRd := True
//  //      //io.ddram.be := U(io.ddram.be.getWidth bits, default -> True)
//  //    }
//  //    when (
//  //      io.ddram.rd
//  //      && io.ddram.doutReady
//  //    ) {
//  //      io.ddram.rd := False
//  //      //io.ddram.be := 0x0
//  //    }
//
//  //    rdDdramFifo.io.push.valid := (
//  //      rDidFirstDdramRd
//  //      && io.ddram.doutReady
//  //    )
//  //    rdDdramFifo.io.push.payload := io.ddram.dout
//  //    when (rdDdramFifo.io.push.fire) {
//  //      rRdDdramBurstCnt := rRdDdramBurstCnt - 1
//  //    }
//  //    when (
//  //      //RegNext(!rRdDdramBurstCnt.orR, init=False)
//  //      //&& rRdDdramBurstCnt.orR
//  //      !rRdDdramBurstCnt.orR
//  //    ) {
//  //      rSeenFinalRdDdramBurstCnt := True
//  //    }
//  //    when (
//  //      //RegNext(!io.ddram.doutReady)
//  //      //&& !rdDdramFifo.io.push.ready
//  //      rSeenFinalRdDdramBurstCnt
//  //      && rRdDdramBurstCnt.orR
//  //    ) {
//  //      rSeenFinalRdDdramBurstCnt := False
//  //      rState := STATE_RD_BURST_POP_FIFO
//  //    }
//  //  }
//  //  is (
//  //    //STATE_RD_BURST_POP_FIFO
//  //    //MaskedLiteral("-100")
//  //    //MaskedLiteral("-010")
//  //    B("0010")
//  //  ) {
//  //    rDidFirstDdramRd := False
//  //    //println(
//  //    //  s"myRdReptCnt.getWidth:${myRdReptCnt.getWidth}"
//  //    //)
//  //    io.lcvBus.d2hBus.src := rSavedLcvH2dPayload.src
//
//  //    myRdReptStm.translateInto(
//  //      io.lcvBus.d2hBus
//  //    )(
//  //      dataAssignment=(outp, inp) => {
//  //        switch (myRdReptCnt) {
//  //          is (0) {
//  //            outp.data := inp(31 downto 0)
//  //          }
//  //          is (1) {
//  //            outp.data := inp(63 downto 32)
//  //          }
//  //        }
//  //      }
//  //    )
//  //    when (
//  //      //!rdDdramFifo.io.pop.valid
//  //      !myRdReptStm.valid
//  //    ) {
//  //      rState := STATE_IDLE
//  //    }
//  //  }
//  //  is (
//  //    //STATE_WR_BURST_PUSH_FIFO
//  //    //MaskedLiteral("-101")
//  //    //MaskedLiteral("-011")
//  //    B("0011")
//  //  ) {
//  //    io.lcvBus.h2dBus.translateInto(
//  //      wrDdramFifo.io.push
//  //    )(
//  //      dataAssignment=(outp, inp) => {
//  //        switch (
//  //          //rWrTempCnt
//  //          inp.addr(
//  //            //log2Up(
//  //            //  inp.data.getWidth >> 3
//  //            //)
//  //            2
//  //          )
//  //        ) {
//  //          is (False) {
//  //            outp.data(31 downto 0) := inp.data
//  //            //outp.byteEn(3 downto 0) := inp.byteEn
//  //          }
//  //          is (True) {
//  //            outp.data(63 downto 32) := inp.data
//  //            //outp.byteEn(7 downto 4) := inp.byteEn
//  //          }
//  //        }
//  //      }
//  //    )
//  //    rDidFirstDdramWe := False
//  //    when (io.lcvBus.h2dBus.burstLast) {
//  //      rState := STATE_WR_BURST_POP_FIFO
//  //    }
//  //  }
//  //  is (
//  //    //Cat(False, STATE_WR_BURST_POP_FIFO.asBits)
//  //    //Cat(False, STATE_WR_BURST_POP_FIFO.asBits)
//  //    B(s"${stateWidth + 1}'d${STATE_WR_BURST_POP_FIFO}")
//  //    //STATE_WR_BURST_POP_FIFO
//  //  ) {
//  //    io.ddram.addr(myDdramAddrRange) := (
//  //      rSavedLcvH2dPayload.addr(myLcvAddrRange)
//  //    )
//  //    //io.ddram.burstCnt := myWrDdramBurstCnt
//  //    io.ddram.din := wrDdramFifo.io.pop.data
//  //    //io.ddram.be := wrDdramFifo.io.pop.byteEn
//  //    //io.ddram.we := True
//
//  //    when (
//  //      //!io.ddram.busy
//  //      //&& rose(rState === STATE_WR_BURST_POP_FIFO)
//  //      //&& 
//  //      !rDidFirstDdramWe
//  //    ) {
//  //      io.ddram.we := True
//  //      rDidFirstDdramWe := True
//  //    }
//  //    //when (io.ddram.we) {
//  //    //  io.ddram.we := False
//  //    //}
//  //    when (
//  //      !wrDdramFifo.io.pop.valid
//  //    ) {
//  //      io.ddram.we := False
//  //    }
//
//  //    switch (
//  //      wrDdramFifo.io.pop.valid
//  //      ## rSeenWrLcvD2hFire
//  //    ) {
//  //      is (B"10") {
//  //        wrDdramFifo.io.pop.ready := True
//  //      }
//  //      is (B"11") {
//  //        wrDdramFifo.io.pop.ready := True
//  //      }
//  //      is (B"01") {
//  //        rState := STATE_IDLE
//  //        //rDidFirstDdramWe := False
//  //      }
//  //      default {
//  //      }
//  //    }
//  //    //when () {
//  //    //  wrDdramFifo.io.pop.ready := True
//  //    //} elsewhen (rSeenWrLcvD2hFire) {
//  //    //  rState := STATE_IDLE
//  //    //}
//  //  }
//  //}
//}

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
	val joystick = in(
	  Vec.fill(6)(
      UInt(32 bits)
    )
	)
	val softReset = in(Vec.fill(2)(
	  Bool()
	))
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
  val ddram = (
    !cfg.inSim
    //&& !cfg.dbgUseLcvBusMem
    //true
  ) generate (
    MeltedMoonDdramIo(cfg=cfg)
  )
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

case class MeltedMoonFbDdram(
  cfg: MeltedMoonConfig
) extends Component {
  //--------
  val io = MeltedMoonIo(cfg=cfg)
  noIoPrefix()
  //--------
  val cartDownload = (
    (
      //(
      //  //(
      //  //  (~io.ioctl.index(5 downto 0).orR)
      //  //  && (io.ioctl.index(7 downto 6) === 0x0)
      //  //)
      //  //||
      //  (
      //    io.ioctl.index(5 downto 0) === 0x1
      //  )
      //)
      //&&
      if (!cfg.dbgUseLcvBusMem) (
        io.ioctl.download
      ) else (
        False
      )
    )
    //|| !io.pllLocked
  )
  val myHistTempSoftReset = (
    Vec.fill(4)(
      History[Vec[Bool]](
        that=(io.softReset),
        length=(
          //2
          //3
          //4
          7
          //1
        ),
        init=(
          //False
          Vec.fill(2)(
            False
          )
        ),
      )//.last
    )
  )
  def myTempSoftReset(idx: Int) = myHistTempSoftReset(idx).last
  //val myTempSoftReset = myHistTempSoftReset.last
  //val myTempSdramCtrlSoftReset = (
  //  rose(myTempSoftReset)
  //  //myTempSoftReset
  //)
  val myTestArea =
    //new Area
    //new ResetArea(
    //  myTempSdramCtrlSoftReset,
    //  cumulative=true//false//true
    //)
    //--------
    // TODO: try `new Area` here...
    //--------
  (
    !cfg.dbgUseLcvBusMem
  ) generate new Area
  {
    val mySdramCtrl = (
      LcvBusSdramCtrl(
        cfg=cfg.sdramCtrlCfg
      )
    )
    mySdramCtrl.io.sdram <> io.sdram
    //mySdramCtrl.io.bus <-/< mySdramCtrlSoftReset.io.hiBus
  }

  def mySdramCtrl = myTestArea.mySdramCtrl
  //--------
  def mySdramCtrlFinalHostIdxIoctl = 0
  def mySdramCtrlFinalHostIdxInternal = 1
  def limMySdramCtrlFinalHostIdx = 2

  //val myTempSdramCtrlFinalBusArbiterSoftReset = (
  //  //rose(myTempSoftReset)
  //  //myTempSoftReset
  //  rose(myHistTempSoftReset(myHistTempSoftReset.size - 2))
  //)

  //val mySdramCtrlFinalBusArbiterArea =
  //  new Area
  //  //new ResetArea(
  //  //  myTempSdramCtrlFinalBusArbiterSoftReset,
  //  //  cumulative=true,
  //  //)
  //{
  //  val arbiter = LcvBusArbiter(
  //    cfg=LcvBusArbiterConfig(
  //      busCfg=cfg.sdramCtrlCfg.busCfg,
  //      numHosts=limMySdramCtrlFinalHostIdx,
  //      kind=(
  //        LcvBusArbiterKind.Priority
  //        //LcvBusArbiterKind.RoundRobin
  //      ),
  //    )
  //  )
  //  //val myInternalSoftReset = (
  //  //  LcvBusDevSoftReset(
  //  //    cfg=LcvBusDevSoftResetConfig(
  //  //      busCfg=cfg.sdramCtrlCfg.busCfg
  //  //    )
  //  //  )
  //  //)

  //  //val rInternalSoftResetState = Reg(Bool(), init=False)

  //  //when (myTempSdramCtrlFinalBusArbiterSoftReset) {
  //  //  rInternalSoftResetState := True
  //  //}
  //  //when (myInternalSoftReset.io.softResetDone) {
  //  //  rInternalSoftResetState := False
  //  //}

  //  arbiter.io.en := True
  //  arbiter.io.forceHost.valid := (
  //    False
  //    //myTempSdramCtrlFinalBusArbiterSoftReset
  //    //|| rInternalSoftResetState
  //    //myInternalSoftReset.io.softResetDone
  //  )
  //  arbiter.io.forceHost.payload := (
  //    0x0
  //    //mySdramCtrlFinalHostIdxIoctl
  //  )

  //  mySdramCtrl.io.bus <-/< arbiter.io.dev
  //  //arbiter.io.hostVec(mySdramCtrlFinalHostIdxInternal) <-/< (
  //  //  myInternalSoftReset.io.hiBus
  //  //)
  //  //myInternalSoftReset.io.softReset := (
  //  //  myTempSdramCtrlFinalBusArbiterSoftReset
  //  //)
  //}
  //def mySdramCtrlFinalBusArbiter = (
  //  mySdramCtrlFinalBusArbiterArea.arbiter
  //)
  //def mySdramCtrlIoctlFinalHost = (
  //  mySdramCtrlFinalBusArbiter.io.hostVec(mySdramCtrlFinalHostIdxIoctl)
  //)
  //def mySdramCtrlInternalFinalHost = (
  //  mySdramCtrlFinalBusArbiter.io.hostVec(mySdramCtrlFinalHostIdxInternal)
  //  //mySdramCtrlFinalBusArbiterArea.myInternalSoftReset.io.loBus
  //)
  //--------
  //def mySdramCtrlHostIdxFbDcache = 0//1//0
  //def mySdramCtrlHostIdxFbInit = 1//2//1
  //def myFbBramHostIdxFbCtrl = 0//1//0
  //def myFbBramHostIdxDcache = 1//2//1
  //def limMyFbBramHostIdx = 2

  def mySdramCtrlHostIdxIoctl = 0//2//0//2
  def mySdramCtrlHostIdxIcache = 2//4//3//2
  def mySdramCtrlHostIdxDcache = 1//3//4//3
  def limMySdramCtrlHostIdx = 3//5//4

  //val myTempSdramCtrlBusArbiterSoftReset = (
  //  rose(myTempSoftReset)
  //  //myTempSoftReset
  //  //rose(myHistTempSoftReset(myHistTempSoftReset.size - 2))
  //)
  val mySdramCtrlBusArbiterArea =
    new Area
    //new ResetArea(
    //  myTempSdramCtrlBusArbiterSoftReset,
    //  cumulative=true,
    //)
  {
    val arbiter = LcvBusArbiter(
      cfg=LcvBusArbiterConfig(
        busCfg=cfg.sdramCtrlCfg.busCfg,
        numHosts=limMySdramCtrlHostIdx,
        kind=(
          LcvBusArbiterKind.Priority
          //LcvBusArbiterKind.RoundRobin
        ),
      )
    )
    arbiter.io.en := True
    arbiter.io.forceHost.valid := False
    arbiter.io.forceHost.payload := 0

    val myDbgLcvBusMem = (
      cfg.dbgUseLcvBusMem
    ) generate {
      //val depth = 1 << (27 - 2) // 128 MiB
      val depth = 1 << (26 - 2) // 64 MiB

      val program = SnowHouseRam32InitFromBin(
        filename=cfg.cpuCfg.programStr
      )

      //val myMemDepth = 0x4000
      val myMemInitBigInt = {
        //val depth = myMemDepth
        val tempArr = new ArrayBuffer[BigInt]()
        tempArr ++= program.view
        while (tempArr.size < depth) {
          tempArr += BigInt(0)
        }
        tempArr
      }

      //val myInitBigInt = {
      //  //val depth = 1 << (16 - 4)

      //  val tempArr = new ArrayBuffer[BigInt]()
      //  tempArr ++= cfg.program.outpArr.view
      //  while (tempArr.size < depth) {
      //    tempArr += BigInt(0)
      //  }
      //  //val programSize = tempArr.size
      //  //for (idx <- programSize until (1 << (16 - 4))) {
      //  //  if (idx < /*1024*/0x800) {
      //  //    //println(
      //  //    //  s"idx < 0x800: ${idx}"
      //  //    //)
      //  //    //tempArr += BigInt(idx)
      //  //    tempArr += BigInt(0)
      //  //  } else {
      //  //    //println(
      //  //    //  s"idx < 0x800: ${idx}"
      //  //    //)
      //  //    //tempArr += BigInt(0)
      //  //  }
      //  //  //tempArr += BigInt(0)
      //  //}
      //  tempArr
      //  //for (elem <- program.outpArr.view) {
      //  //  tempArr +=
      //  //}
      //  //program.outpArr
      //}
      LcvBusMem(
        cfg=LcvBusMemConfig(
          busCfg=cfg.sdramCtrlCfg.busCfg,
          depth=depth,
          initBigInt=Some(myMemInitBigInt),
        )
      )
    }
    if (!cfg.dbgUseLcvBusMem) {
      mySdramCtrl.io.bus <-/< arbiter.io.dev
    } else {
      myDbgLcvBusMem.io.bus <-/< arbiter.io.dev
    }

    //mySdramCtrlInternalFinalHost <-/< arbiter.io.dev
  }

  //val mySoftResetCntMaxVal = ((1.0 ms) * cfg.clkRate).toLong
  //val rMySoftResetCnt = (
  //  Vec.fill(myHistTempSoftReset.size)(
  //    Reg(SInt((log2Up(mySoftResetCntMaxVal + 1) + 1) bits))
  //    init(mySoftResetCntMaxVal)
  //  )
  //)
  //for (idx <- 0 until rMySoftResetCnt.size) {
  //  when (
  //    //rose(
  //      myTempSoftReset(idx)
  //    //)
  //  ) {
  //    rMySoftResetCnt(idx) := mySoftResetCntMaxVal
  //  } elsewhen (!rMySoftResetCnt(idx).msb) {
  //    rMySoftResetCnt(idx) := rMySoftResetCnt(idx) - 1
  //  }
  //}

  def mkMyCpuAreaSoftReset(idx: Int, jdx: Int) = (
    //rose(myTempSoftReset)
    myTempSoftReset(idx=idx)(jdx)
    //|| 
    //!rMySoftResetCnt(idx).msb
    //|| 
    //(
    //  RegNextWhen(
    //    False,
    //    cond=(
    //      //io.softReset
    //      //fell(myTempSoftReset(idx=idx))
    //      myTempSoftReset(idx=idx)(jdx)
    //      //!rMySoftResetCnt(idx).msb
    //    ),
    //    init=True
    //  )
    //)
  )

  //val myCpuFbDcacheSoftReset = (
  //  LcvBusDevSoftReset(
  //    cfg=LcvBusDevSoftResetConfig(
  //      //busCfg=cfg.sdramCtrlCfg.busCfg
  //      busCfg=(
  //        cfg.myFbCtrlMmapCfg.busCfg
  //      ),
  //    )
  //  )
  //)
  //myCpuFbDcacheSoftReset.io.softReset := mkMyCpuAreaSoftReset(1)

  //val myCpuIcacheSoftReset = (
  //  LcvBusDevSoftReset(
  //    cfg=LcvBusDevSoftResetConfig(
  //      busCfg=cfg.sdramCtrlCfg.busCfg
  //    )
  //  )
  //)
  //myCpuIcacheSoftReset.io.softReset := mkMyCpuAreaSoftReset(3)

  //val myCpuNonFbDcacheSoftReset = (
  //  LcvBusDevSoftReset(
  //    cfg=LcvBusDevSoftResetConfig(
  //      busCfg=cfg.sdramCtrlCfg.busCfg
  //    )
  //  )
  //)
  //myCpuNonFbDcacheSoftReset.io.softReset := mkMyCpuAreaSoftReset(2)


  def mySdramCtrlBusArbiter = (
    mySdramCtrlBusArbiterArea.arbiter
  )
  //def mySdramCtrlFbDcacheHost = (
  //  mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxFbDcache)
  //)
  //def mySdramCtrlFbInitHost = (
  //  mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxFbInit)
  //)
  def mySdramCtrlIoctlFinalHost = (
    //mySdramCtrlFinalBusArbiter.io.hostVec(mySdramCtrlFinalHostIdxIoctl)
    mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxIoctl)
  )
  if (cfg.dbgUseLcvBusMem) {
    mySdramCtrlIoctlFinalHost.h2dBus.valid := False
    mySdramCtrlIoctlFinalHost.h2dBus.payload := (
      mySdramCtrlIoctlFinalHost.h2dBus.payload.getZero
    )
    mySdramCtrlIoctlFinalHost.d2hBus.ready := False
  }
  def mySdramCtrlIcacheHost = (
    mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxIcache)
    //myCpuIcacheSoftReset.io.loBus
  )
  def mySdramCtrlDcacheHost = (
    mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxDcache)
    //myCpuDcacheSoftReset.io.loBus
  )

  //mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxIcache) <-/< (
  //  myCpuIcacheSoftReset.io.hiBus
  //)
  //mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxDcache) <-/< (
  //  myCpuNonFbDcacheSoftReset.io.hiBus
  //)

  //--------
  //val myTempVgaSoftReset = (
  //  rose(myTempSoftReset(idx=0))
  //  //rose(myHistTempSoftReset(myHistTempSoftReset.size - 2))
  //)
  //val vgaClkDomain = ClockDomain.external(
  //  name="vgaClk",
  //  config=ClockDomainConfig(
  //    resetKind=SYNC,
  //  ),
  //  withReset=true,
  //  withSoftReset=false,
  //  withClockEnable=false,
  //  frequency=FixedFrequency(cfg.mainVgaClkRate),
  //)
  //val vgaPushFifo = StreamFifoCC(
  //  dataType=Rgb(cfg.myFbCfg.rgbCfg),
  //  depth=(
  //    //32
  //    4
  //  ),
  //  pushClock=ClockDomain.current,
  //  popClock=vgaClkDomain,
  //)

  val rMyDbgResetState = (
    Reg(Bool(), init=True)
  )
  when (
    //fell(io.ioctl.download)
    if (
      io.ddram != null
    ) (
      fell(io.joystick(0)(0))
    ) else (
      True
    )
  ) {
    rMyDbgResetState := False
  }

  val rMyCpuResetState = (
    Reg(Bool(),
      init=(
        //if (!cfg.dbgUseLcvBusMem) (
          True
        //) else (
        //  False
        //)
      )
    )
  )

  val vgaPushInpStm = Stream(Rgb(cfg.myFbCfg.rgbCfg))
  val vgaPushOutpStm = (
    Stream(Rgb(cfg.myFbCfg.rgbCfg))
    //StreamCCByToggle(
    //  input=vgaPushInpStm,
    //  inputClock=ClockDomain.current,
    //  outputClock=vgaClkDomain,
    //)
  )
  vgaPushOutpStm <-/< vgaPushInpStm
  //val vgaPushFifo = StreamFifoCC(
  //  dataType=Rgb(cfg.myFbCfg.rgbCfg),
  //  depth=(
  //    //32
  //    4
  //  ),
  //  pushClock=ClockDomain.current,
  //  popClock=vgaClkDomain,
  //)
  val vgaArea =
    new Area
    //new ResetArea(
    //  myTempVgaReset,
    //  cumulative=true,
    //)
    //new ClockingArea(
    //  clockDomain=vgaClkDomain
    //)
  {
    val vgaTimingInfo = cfg.vgaTimingInfo
    //val vgaCtrl = VgaCtrl(rgbConfig=cfg.rgbCfg)
    val lcvVgaCtrl = (
      LcvVgaCtrl(
        clkRate=cfg.mainVgaClkRate,
        rgbConfig=cfg.rgbCfg,
        vgaTimingInfo=vgaTimingInfo,
        fifoDepth=(
          1
        ),
      )
    )
    lcvVgaCtrl.io.fifoFlush := False
    //vgaCtrl.io.softReset := False//myTempVgaSoftReset

    //vgaCtrl.io.timings.setAs_h640_v480_r60
    //vgaTimingInfo.driveSpinalVgaTimings(
    //  clkRate=cfg.clkRate,
    //  spinalVgaTimings=vgaCtrl.io.timings
    //)
    lcvVgaCtrl.io.push <-/< vgaPushOutpStm //vgaPushFifo.io.pop
    //vgaCtrl.io.push <-/< vgaPushOutpStm //vgaPushFifo.io.pop

    //vgaArea.vgaCtrl.io.pixels <-/< vgaPushFifo.io.pop

    io.vgaPhys.setAsReg() init(io.vgaPhys.getZero)
    io.vgaVisib.setAsReg() init(io.vgaVisib.getZero)
    io.vgaPixelEn.setAsReg() init(io.vgaPixelEn.getZero)
    val cpp = LcvVgaCtrl.cpp(
      clkRate=cfg.mainVgaClkRate,
      vgaTimingInfo=vgaTimingInfo
    )
    val rPixelEnCnt = (
      Reg(UInt(
        LcvVgaCtrl.clkCntWidth(
          clkRate=cfg.mainVgaClkRate,
          vgaTimingInfo=vgaTimingInfo
        ) bits
      ))
      init(0x0)
    )
    when (rPixelEnCnt.resize(rPixelEnCnt.getWidth + 1) + 1 < cpp) {
      rPixelEnCnt := rPixelEnCnt + 1
    } otherwise {
      rPixelEnCnt := 0x0
    }

    io.vgaPhys.hsync := (
      lcvVgaCtrl.io.phys.hsync
      //vgaCtrl.io.vga.hSync
    )
    io.vgaPhys.vsync := (
      lcvVgaCtrl.io.phys.vsync
      //vgaCtrl.io.vga.vSync
    )
    io.vgaVisib := (
      lcvVgaCtrl.io.misc.visib
      //vgaCtrl.io.vga.colorEn
    )
    io.vgaPixelEn := (
      lcvVgaCtrl.io.misc.pixelEn
      //rPixelEnCnt === cpp - 1
    )

    lcvVgaCtrl.io.en := True
    when (
      lcvVgaCtrl.io.misc.visib
      //vgaCtrl.io.vga.colorEn
    ) {
      io.vgaPhys.col.r(2 downto 0) := 0x7
      io.vgaPhys.col.r(7 downto 3) := (
        lcvVgaCtrl.io.phys.col.r
        //vgaCtrl.io.vga.color.r
      )
      io.vgaPhys.col.g(2 downto 0) := 0x7
      io.vgaPhys.col.g(7 downto 3) := (
        lcvVgaCtrl.io.phys.col.g
        //vgaCtrl.io.vga.color.g
      )
      io.vgaPhys.col.b(2 downto 0) := 0x7
      io.vgaPhys.col.b(7 downto 3) := (
        lcvVgaCtrl.io.phys.col.b
        //vgaCtrl.io.vga.color.b
      )
    } otherwise {
      io.vgaPhys.col := io.vgaPhys.col.getZero
    }

    val myDoVblankIrq = Bool()
    myDoVblankIrq := (
      rose(
        RegNext(
          (lcvVgaCtrl.io.misc.vpipeS === LcvVgaState.front),
          //vgaCtrl.io.frameStart,
          init=False,
        )
      )
    )
  }
  //--------
  //val myTempFbInitSoftReset = (
  //  rose(myHistTempSoftReset(myHistTempSoftReset.size - 2))
  //)

  //val fbInitArea =
  //  new Area
  //  //new ResetArea(
  //  //  myTempFbInitSoftReset,
  //  //  cumulative=true
  //  //)
  //{
  //  val vgaTimingInfo = cfg.vgaTimingInfo
  //  val fbSize2d = cfg.myFbCfg.fbSize2d

  //  val mySdramSizeBytes = 1 << cfg.myDbusSlicerAddrSliceHi
  //  val mySdramSizeWords = mySdramSizeBytes >> 2
  //  val rCnt = (
  //    //Reg(UInt(log2Up(fbSize2d.y * fbSize2d.x + 1) + 1 bits))
  //    Reg(UInt(log2Up(mySdramSizeWords) + 1 bits))
  //    init(0x0)
  //  )
  //  val myTempH2dStm = cloneOf(mySdramCtrlFbInitHost.h2dBus)
  //  mySdramCtrlFbInitHost.h2dBus <-/< myTempH2dStm
  //  mySdramCtrlFbInitHost.d2hBus.ready := True


  //  myTempH2dStm.valid := (
  //    //(rCnt < ((fbSize2d.y * fbSize2d.x) >> 1))
  //    //rCnt < mySdramSizeWords
  //    False
  //  )
  //  myTempH2dStm.addr := 0x0
  //  myTempH2dStm.addr.allowOverride
  //  //myTempH2dStm.addr(cfg.fbAddrSliceHi) := True
  //  myTempH2dStm.addr(rCnt.high + 2 downto 2) := rCnt
  //  myTempH2dStm.byteEn := (
  //    U(myTempH2dStm.byteEn.getWidth bits, default -> True)
  //  )
  //  myTempH2dStm.data := (
  //    0x0
  //    U(
  //      myTempH2dStm.data.getWidth bits,
  //      // two blank pixels
  //      31 -> False,
  //      15 -> False,
  //      default -> //True//False//True
  //    )
  //  )
  //  myTempH2dStm.src := 0x0
  //  myTempH2dStm.isWrite := True
  //  when (myTempH2dStm.fire) {
  //    rCnt := rCnt + 1
  //  }
  //  //when (
  //  //  //rose(io.softReset)
  //  //  rose(myTempSoftReset)
  //  //) {
  //  //  rCnt := 0x0
  //  //} elsewhen (myTempH2dStm.fire) {
  //  //  rCnt := rCnt + 1
  //  //}
  //  myTempH2dStm.burstFirst := (
  //    myTempH2dStm.addr(5 downto 2) === 0x0
  //  )
  //  myTempH2dStm.burstLast := (
  //    myTempH2dStm.addr(5 downto 2) === 0xf
  //  )
  //  myTempH2dStm.burstCnt := 15
  //}
  //--------
  //val myTempFbAndDcacheSoftReset = (
  //  rose(myHistTempSoftReset(myHistTempSoftReset.size - 2))
  //)

  val fbAndDcacheArea = 
    new Area
    //new ResetArea(
    //  myTempFbAndDcacheSoftReset,
    //  cumulative=true,
    //)
  {
    //--------
    //--------
    //val myFbDcache = (
    //  LcvBusCache(cfg=LcvBusCacheBusPairConfig(
    //    mainCfg=cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg.mainCfg,
    //    loBusCacheCfg=LcvBusCacheConfig(
    //      kind=LcvCacheKind.D,
    //      lineSizeBytes=64,
    //      depthWords=(
    //        //4 * 1024 / (4 * 2)
    //        256
    //        //64
    //        //128
    //      ).toInt,
    //      numCpus=1,
    //      lineWordMemRamStyleAltera=(
    //        "no_rw_check, M10K"
    //      ),
    //      lineAttrsMemRamStyleAltera=(
    //        //"no_rw_check, MLAB"
    //        "no_rw_check, M10K"
    //      ),
    //    ),
    //    hiBusCacheCfg=None,
    //  ))
    //)
    val myFbDcache = (
      LcvBusCache(cfg=LcvBusCacheBusPairConfig(
        mainCfg=cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg.mainCfg,
        loBusCacheCfg=LcvBusCacheConfig(
          kind=LcvCacheKind.D,
          lineSizeBytes=64,
          depthWords=(
            //2048
            1024
            //4 * 1024 /// (4 * 2)
            //256
            //64
            //128
          ).toInt,
          numWays=(
            //1
            2
          ),
          numCpus=1,
          lineWordMemRamStyleAltera=(
            "no_rw_check, M10K"
          ),
          lineAttrsMemRamStyleAltera=(
            //"no_rw_check, MLAB"
            "no_rw_check, M10K"
          ),
        ),
        hiBusCacheCfg=None,
      ))
    )

    //mySdramCtrlFbDcacheHost <-/< myFbDcache.io.hiBus
    //val myDbgFbDcacheByteEnAdapter = (
    //  LcvBusSlowNonBurstingByteEnAdapter(
    //    cfg=LcvBusByteEnAdapterConfig(
    //      loBusCfg=cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg.loBusCfg
    //    )
    //  )
    //)
    //myDbgFbDcacheByteEnAdapter.io.loBus.h2dBus << (
    //  mySdramCtrlFbDcacheHost.h2dBus
    //)
    //mySdramCtrlFbDcacheHost.d2hBus << (
    //  myDbgFbDcacheByteEnAdapter.io.loBus.d2hBus
    //)
    //myDbgFbDcacheByteEnAdapter.io.hiBus.h2dBus.translateInto(
    //  mySdramCtrlFbDcacheHost.h2dBus
    //)(
    //  dataAssignment=(outp, inp) => {
    //    outp.mainNonBurstInfo := inp.mainNonBurstInfo
    //    outp.mainBurstInfo := outp.mainBurstInfo.getZero
    //  }
    //)
    //mySdramCtrlFbDcacheHost.d2hBus.translateInto(
    //  myDbgFbDcacheByteEnAdapter.io.hiBus.d2hBus
    //)(
    //  dataAssignment=(outp, inp) => {
    //    outp.mainNonBurstInfo := inp.mainNonBurstInfo
    //    //outp.mainBurstInfo := outp.mainBurstInfo.getZero
    //  }
    //)

    //--------
    def myFbArbiterHostIdxFbCtrl = 0
    def myFbArbiterHostIdxFbInit = 1
    def myFbArbiterHostIdxCpu = 2//1
    def limMyFbArbiterHostIdx = 3//2

    val myFbDeburster = LcvBusDeburster(
      cfg=LcvBusDebursterConfig(
        loBusCfg=cfg.myFbCtrlMmapCfg.busCfg
      )
    )

    val myFbArbTempArea =
      //new ResetArea(
      //  myTempFbAndDcacheSoftReset,
      //  cumulative=true,
      //)
      new Area
    {
      //val myFbBram = {
      //  val depth = (
      //    ((cfg.myFbCfg.fbSize2d.x * cfg.myFbCfg.fbSize2d.y) >> 1)
      //    //((cfg.myFbCfg.fbSize2d.x * cfg.myFbCfg.fbSize2d.y) >> 2)
      //  )
      //  //val myMemInitBigInt = {
      //  //  //val depth = myMemDepth
      //  //  
      //  //  val tempArr = new ArrayBuffer[BigInt]()
      //  //  //tempArr ++= program.view
      //  //  while (tempArr.size < depth) {
      //  //    tempArr += BigInt(0)
      //  //  }
      //  //  tempArr
      //  //}
      //  LcvBusMem(
      //    cfg=LcvBusMemConfig(
      //      busCfg=LcvBusConfig(
      //        //cfg.sdramCtrlCfg.busCfg
      //        mainCfg=LcvBusMainConfig(
      //          dataWidth=32,
      //          addrWidth=32,
      //          allowBurst=true,
      //          burstAlwaysMaxSize=true,
      //          srcWidth=cfg.sdramCtrlCfg.busCfg.srcWidth,
      //          haveByteEn=false,
      //          keepByteSize=false,
      //        ),
      //      ),
      //      depth=depth,
      //      initBigInt=(
      //        //Some(myMemInitBigInt)
      //        None
      //      ),
      //    )
      //  )
      //}

      val arbiter = LcvBusArbiter(
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
      arbiter.io.en := True
      arbiter.io.forceHost.valid := False
      arbiter.io.forceHost.payload := 0
      myFbDeburster.io.loBus <-/< arbiter.io.dev
      myFbDcache.io.loBus <-/< myFbDeburster.io.hiBus

      //--------
      //val myBramFb = MeltedMoonBramFb(cfg=cfg)

      //myFbBram.io.bus <-/< arbiter.io.dev

      val myLcvBusToDdramBridge = MeltedMoonLcvBusToDdramBridge(cfg=cfg)
      io.ddram <> myLcvBusToDdramBridge.io.ddram

      val myDdramResetArea =
      new ResetArea(
        reset=(
          //fell(
          //  io.joystick(0)(0)
          //)
          rMyDbgResetState
        ),
        cumulative=(
          false
        )
      ) {
        myLcvBusToDdramBridge.io.lcvBus <-/< (
          myFbDcache.io.hiBus
          //arbiter.io.dev
        )
      }

      //--------

      //mySdramCtrlFbDcacheHost <-/< myFbDeburster.io.hiBus

      //myDbgFbDcacheByteEnAdapter.io.loBus.h2dBus << (
      //  arbiter.io.dev.h2dBus
      //)
      //arbiter.io.dev.d2hBus << (
      //  myDbgFbDcacheByteEnAdapter.io.loBus.d2hBus
      //)
      //myFbDeburster.io.hiBus.h2dBus.translateInto(
      //  myDbgFbDcacheByteEnAdapter.io.loBus.h2dBus
      //)(
      //  dataAssignment=(outp, inp) => {
      //    outp.mainNonBurstInfo := inp.mainNonBurstInfo
      //    //outp.mainBurstInfo := outp.mainBurstInfo.getZero
      //  }
      //)
      //myDbgFbDcacheByteEnAdapter.io.loBus.d2hBus.translateInto(
      //  myFbDeburster.io.hiBus.d2hBus
      //)(
      //  dataAssignment=(outp, inp) => {
      //    outp.mainNonBurstInfo := inp.mainNonBurstInfo
      //    //outp.mainBurstInfo := outp.mainBurstInfo.getZero
      //  }
      //)
    }

    def myFbArbiter = myFbArbTempArea.arbiter

    def myFbArbFbCtrlHost = (
      myFbArbiter.io.hostVec(myFbArbiterHostIdxFbCtrl)
    )
    def myFbArbFbInitHost = (
      myFbArbiter.io.hostVec(myFbArbiterHostIdxFbInit)
    )
    def myFbArbCpuHost = (
      myFbArbiter.io.hostVec(myFbArbiterHostIdxCpu)
      //myCpuFbDcacheSoftReset.io.loBus
    )
    //myFbArbTempArea.arbiter.io.hostVec(myFbArbiterHostIdxCpu) <-/< (
    //  myCpuFbDcacheSoftReset.io.hiBus
    //)

    //vgaArea.lcvVgaCtrl.io.push <-/< myFbCtrl.io.pop
    //vgaPushFifo.io.push <-/< myFbCtrl.io.pop

    //vgaArea.vgaCtrl.io.pixels <-/< myFbCtrl.io.pop

    val myFbCtrlResetArea = 
    new ResetArea(
      reset=(
        //fell(
        //  io.joystick(0)(0)
        //)
        rMyDbgResetState
      ),
      cumulative=(
        false
      )
    ) {
      val myFbCtrl = LcvBusFramebufferCtrl(cfg=cfg.myFbCfg)
      vgaPushInpStm <-/< myFbCtrl.io.pop
      myFbArbFbCtrlHost <-/< myFbCtrl.io.bus
    }
    when (rMyDbgResetState) {
      vgaPushInpStm.valid := rMyDbgResetState
      vgaPushInpStm.payload.r := 0x1f
      vgaPushInpStm.payload.g := 0x1f
      vgaPushInpStm.payload.b := 0x1f
    } otherwise {
      //vgaPushInpStm <-/< myFbCtrlResetArea.myFbCtrl.io.pop
    }

  }
  //--------
  val fbInitArea =
    //new Area
    new ResetArea(
      //myTempFbInitSoftReset,
      rMyDbgResetState,
      cumulative=(
        //true
        false
      )
    )
  {
    def myFbArbFbInitHost = fbAndDcacheArea.myFbArbFbInitHost
    val vgaTimingInfo = cfg.vgaTimingInfo
    val fbSize2d = cfg.myFbCfg.fbSize2d

    //val mySdramSizeBytes = 1 << cfg.myDbgDbusSlicerAddrSliceHi
    //val mySdramSizeWords = mySdramSizeBytes >> 2
    val rCnt = (
      Reg(UInt(log2Up(fbSize2d.y * fbSize2d.x + 1) + 1 bits))
      //Reg(UInt(log2Up(mySdramSizeWords) + 1 bits))
      init(0x0)
    )
    val myTempH2dStm = (
      cloneOf(myFbArbFbInitHost.h2dBus)
    )
    myFbArbFbInitHost.h2dBus <-/< myTempH2dStm
    myFbArbFbInitHost.d2hBus.ready := True

    myTempH2dStm.valid := (
      (rCnt < ((fbSize2d.y * fbSize2d.x) >> 1))
      //rCnt < mySdramSizeWords
      //False
    )
    myTempH2dStm.addr := 0x0
    myTempH2dStm.addr.allowOverride
    myTempH2dStm.addr(cfg.fbAddrSliceHi) := True
    myTempH2dStm.addr(rCnt.high + 2 downto 2) := rCnt
    //myTempH2dStm.byteEn := (
    //  U(myTempH2dStm.byteEn.getWidth bits, default -> True)
    //)
    myTempH2dStm.byteSize := 0x2 // 32-bit

    val myPrngCfg = LcvXorShift16Config(
      xsInitS2d={
        val tempA2d = new ArrayBuffer[Seq[BigInt]]()
        val outerSize = (
          //cfg.busCfg.maxBurstSizeMinus1 + 1
          //+ (if (cfg.kind._hasRandAddr) (1) else (0))
          //16
          //4
          //1
          //2
          1
        )
        val innerSize = 2//1//2
        for (idx <- 0 until outerSize) {
          val tempArr = new ArrayBuffer[BigInt]()
          for (jdx <- 0 until innerSize) {
            tempArr += (
              BigInt(idx) * BigInt(innerSize) + BigInt(jdx) + 1
            )
          }
          tempA2d += tempArr
        }
        tempA2d
      }
    )
    val myPrng = LcvXorShift16(cfg=myPrngCfg)
    val tempCol = Rgb(cfg.myFbCfg.rgbCfg)
    val duplCol = UInt(cfg.myFbCfg.rgbCfg.rWidth bits)
    duplCol := myPrng.io.outpXs.asBits.asUInt(duplCol.bitsRange)
    tempCol.r := duplCol
    tempCol.g := duplCol
    tempCol.b := duplCol
    myTempH2dStm.data := (
      //0x0
      //Cat(
        //myPrng.io.outpXs.asBits.asUInt
        Cat(
          False,
          tempCol,
          False,
          tempCol
        ).asUInt
      //).asUInt
      //U(
      //  myTempH2dStm.data.getWidth bits,
      //  // two blank pixels
      //  31 -> False,
      //  15 -> False,
      //  default -> True//False//True
      //)
    )
    myTempH2dStm.src := 0x0
    myTempH2dStm.isWrite := True
    when (
      myTempH2dStm.fire
    ) {
      rCnt := rCnt + 1
    }
    //when (
    //  //rose(io.softReset)
    //  rose(myTempSoftReset)
    //) {
    //  rCnt := 0x0
    //} elsewhen (myTempH2dStm.fire) {
    //  rCnt := rCnt + 1
    //}
    myTempH2dStm.burstFirst := (
      myTempH2dStm.addr(5 downto 2) === 0x0
    )
    myTempH2dStm.burstLast := (
      myTempH2dStm.addr(5 downto 2) === 0xf
    )
    myTempH2dStm.burstCnt := 15
  }
  //--------

  //val rSeenCpuAreaSoftReset = (
  //  //Vec.fill(2)(
  //    Reg(Bool(), init=False)
  //  //)
  //)
  //when (rMyCpuResetState) {
  //  when (!rSeenCpuAreaSoftReset) {
  //    when (mkMyCpuAreaSoftReset(0, 0)) {
  //      rSeenCpuAreaSoftReset := True
  //    }
  //  } otherwise { // when (rSeenCpuAreaSoftReset)
  //    when (mkMyCpuAreaSoftReset(0, 1)) {
  //      rMyCpuResetState := False
  //    }
  //  }
  //}

  //when (
  //  fell(io.ioctl.download)
  //) {
  //  rMyCpuResetState := False
  //}

  //switch (rMyCpuResetState) {
  //  is (False) {
  //  }
  //  is (True) {
  //  }
  //}
  //when (rSeenCpuAreaSoftReset(0)) {
  //}
  //when (mkMyCpuAreaSoftReset(0, 0)) {
  //  rMyCpuResetState := True
  //}
  //when (
  //  myTempSoftReset
  //  && mkMyCpuAreaSoftReset(0, 1)
  //) {
  //  rMyCpuResetState := False
  //}
  val cpuArea =
    new Area
    //new ResetArea(
    //  //myCpuAreaSoftReset,
    //  ////mkMyCpuAreaSoftReset(0),
    //  History[Bool](
    //    that=rMyCpuResetState,
    //    length=7,
    //    init=True,
    //  ).last,
    //  //fell(rMyCpuResetState),
    //  cumulative=(
    //    if (!cfg.dbgUseLcvBusMem) (
    //      false//true//false//true//false//true
    //    ) else (
    //      true
    //    )
    //  )
    //)
    //new ResetArea(
    //  //myCpuAreaSoftReset,
    //  ////mkMyCpuAreaSoftReset(0),
    //  History[Bool](
    //    that=rMyCpuResetState,
    //    length=7,
    //    init=True,
    //  ).last,
    //  //fell(rMyCpuResetState),
    //  cumulative=(
    //    if (!cfg.dbgUseLcvBusMem) (
    //      false//true//false//true//false//true//false//true
    //    ) else (
    //      true
    //    )
    //  )
    //)
  {
    //--------
    val myCpuInnerArea =
      //new Area
      //new ResetArea(
      //  //myCpuAreaSoftReset,
      //  mkMyCpuAreaSoftReset(0),
      //  cumulative=true//false//true
      //)
      new ResetArea(
        //myCpuAreaSoftReset,
        ////mkMyCpuAreaSoftReset(0),
        History[Bool](
          that=rMyCpuResetState,
          length=7,
          init=True,
        ).last,
        //fell(rMyCpuResetState),
        cumulative=(
          if (!cfg.dbgUseLcvBusMem) (
            false//true//false//true//false//true
          ) else (
            true
          )
        )
      )
    {
      val cpu = (
        //SnowHouseCpuWithoutRam(program=cfg.testProgram.program)
        SnowHouseRiscv32imWithoutRam(
          //program=cfg.testProgram.program
          cfg=cfg.cpuCfg
        )
      )
    }
    def cpu = myCpuInnerArea.cpu
    //--------
    //val irqCtrl = LcvBusIrqCtrl(
    //  cfg=LcvBusIrqCtrlConfig(
    //    busCfg=cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg.loBusCfg,
    //    depth=2,
    //  )
    //)

    //cpu.io.idsIraIrq.nextValid := (
    //  //!myCpuAreaSoftReset
    //  //&& 
    //  irqCtrl.io.dstIrq.nextValid
    //)
    //irqCtrl.io.dstIrq.ready := (
    //  //!myCpuAreaSoftReset
    //  //&& 
    //  cpu.io.idsIraIrq.ready
    //)

    //def myDoVblankIrq = vgaArea.myDoVblankIrq
    //irqCtrl.io.srcIrqVec(0) := (
    //  //!myCpuAreaSoftReset
    //  //&& 
    //  myDoVblankIrq
    //)

    //val myTimerIrqOverflow = (
    //  floor((cfg.clkRate / (1.0 kHz)).toDouble).toInt
    //)
    //val rTimerIrqCnt = (
    //  Reg(UInt(log2Up(myTimerIrqOverflow + 1) + 1 bits))
    //  init(0x0)
    //)
    //when (rTimerIrqCnt < myTimerIrqOverflow - 1) {
    //  rTimerIrqCnt := rTimerIrqCnt + 1
    //  irqCtrl.io.srcIrqVec(1) := False
    //} otherwise {
    //  rTimerIrqCnt := 0x0
    //  irqCtrl.io.srcIrqVec(1) := True//!myCpuAreaSoftReset //True
    //}
    //--------
    val icache = (
      LcvBusCache(cfg=cfg.cpuCfg.shCfg.subCfg.lcvIbusEtcCfg)
      //LcvBusCache(cfg=LcvBusCacheBusPairConfig(
      //  mainCfg=cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg.mainCfg,
      //  loBusCacheCfg=LcvBusCacheConfig(
      //    kind=LcvCacheKind.I,
      //    lineSizeBytes=64,
      //    depthWords=(
      //      //4 * 1024 / (4 * 2)
      //      //256
      //      //64
      //      //128
      //      512 // 2 kiB data cache
      //    ).toInt,
      //    numCpus=1,
      //    lineWordMemRamStyleAltera=(
      //      "no_rw_check, M10K"
      //    ),
      //    lineAttrsMemRamStyleAltera=(
      //      //"no_rw_check, MLAB"
      //      "no_rw_check, M10K"
      //    ),
      //  ),
      //  hiBusCacheCfg=None,
      //))
    )
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
        //val tempCond = (
        //  inp.src
        //  === (
        //    RegNextWhen(
        //      inp.src,
        //      cond=icache.io.loBus.d2hBus.fire,
        //    )
        //    init(0x2)
        //  )
        //)
        //val rState = Reg(Bool(), init=False)
        //when (
        //  !myHistCpuIbusD2hFire.last
        //  || (
        //    tempCond
        //    && !rState
        //  )
        //) {
        //  outp.data := outp.data.getZero
        //  when (inp.src === 0x3) {
        //    rState := True
        //  }
        //}
      }
    )
    cpu.io.lcvIbus.d2hBus <-/< myTempCpuLcvIbusD2hStm

    mySdramCtrlIcacheHost <-/< icache.io.hiBus

    //val myDbgIcacheByteEnAdapter = (
    //  LcvBusSlowNonBurstingByteEnAdapter(
    //    cfg=LcvBusByteEnAdapterConfig(
    //      loBusCfg=cfg.cpuCfg.shCfg.subCfg.lcvIbusEtcCfg.loBusCfg
    //    )
    //  )
    //)
    //myDbgIcacheByteEnAdapter.io.loBus.h2dBus << cpu.io.lcvIbus.h2dBus
    //cpu.io.lcvIbus.d2hBus <-/< myDbgIcacheByteEnAdapter.io.loBus.d2hBus

    //myDbgIcacheByteEnAdapter.io.hiBus.h2dBus.translateInto(
    //  mySdramCtrlIcacheHost.h2dBus
    //)(
    //  dataAssignment=(outp, inp) => {
    //    outp.mainNonBurstInfo := inp.mainNonBurstInfo
    //    outp.mainBurstInfo := outp.mainBurstInfo.getZero
    //  }
    //)
    //mySdramCtrlIcacheHost.d2hBus.translateInto(
    //  myDbgIcacheByteEnAdapter.io.hiBus.d2hBus
    //)(
    //  dataAssignment=(outp, inp) => {
    //    outp.mainNonBurstInfo := inp.mainNonBurstInfo
    //  }
    //)

    //--------
    val myDbgDbusSlicer = LcvBusSlicer(
      cfg=LcvBusSlicerConfig(
        mmapCfg=(
          //cfg.myFbDbusSlicerMmapCfg
          cfg.myDbgDbusSlicerMmapCfg
          //cfg.myFrontDbusSlicerMmapCfg
        ),
        maxNumOutstandingTxns=(
          // if this doesn't work, try increasing it.
          // It has been reduced to shrink the size of the counter for fmax
          // purposes
          4
        ),
      )
    )
    //val myFrontDbusSlicer = LcvBusSlicer(
    //  cfg=LcvBusSlicerConfig(
    //    mmapCfg=(
    //      //cfg.myFbDbusSlicerMmapCfg
    //      //cfg.myDbusSlicerMmapCfg
    //      cfg.myFrontDbusSlicerMmapCfg
    //    ),
    //    maxNumOutstandingTxns=(
    //      // if this doesn't work, try increasing it.
    //      // It has been reduced to shrink the size of the counter for fmax
    //      // purposes
    //      4
    //    ),
    //  )
    //)
    //val myBackDbusSlicer = LcvBusSlicer(
    //  cfg=LcvBusSlicerConfig(
    //    mmapCfg=cfg.myBackDbusSlicerMmapCfg,
    //    maxNumOutstandingTxns=(
    //      // if this doesn't work, try increasing it.
    //      // It has been reduced to shrink the size of the counter for fmax
    //      // purposes
    //      4
    //    ),
    //  )
    //)

    val myFbCpuHostClone = cloneOf(fbAndDcacheArea.myFbArbCpuHost)

    fbAndDcacheArea.myFbArbCpuHost <-/< myFbCpuHostClone

    def mySlicedNonFbDcacheHost = myDbgDbusSlicer.io.devVec(
      cfg.myNonFbSdramAddrDbgSliceVal
    )
    def mySlicedFbHost = myDbgDbusSlicer.io.devVec(
      cfg.myFbAddrDbgSliceVal
    )
    def mySlicedIoIrqCtrlHost = myDbgDbusSlicer.io.devVec(
      cfg.myIoIrqCtrlAddrDbgSliceVal
    )
    def mySlicedIoNonIrqMmioHost = myDbgDbusSlicer.io.devVec(
      cfg.myIoNonIrqMmioAddrDbgSliceVal
    )
    //def mySlicedNonFbDcacheHost = myFrontDbusSlicer.io.devVec(
    //  cfg.myNonFbSdramAddrFrontSliceVal
    //)
    //def mySlicedFbHost = myBackDbusSlicer.io.devVec(
    //  cfg.myFbAddrBackSliceVal
    //)
    //def mySlicedIoIrqCtrlHost = myBackDbusSlicer.io.devVec(
    //  cfg.myIoIrqCtrlAddrBackSliceVal
    //)
    //def mySlicedIoNonIrqMmioHost = myBackDbusSlicer.io.devVec(
    //  cfg.myIoNonIrqMmioAddrBackSliceVal
    //)

    mySlicedFbHost.h2dBus.translateInto(myFbCpuHostClone.h2dBus)(
      dataAssignment=(outp, inp) => {
        outp.mainNonBurstInfo := inp.mainNonBurstInfo
        outp.mainBurstInfo := outp.mainBurstInfo.getZero
      }
    )
    myFbCpuHostClone.d2hBus.translateInto(mySlicedFbHost.d2hBus)(
      dataAssignment=(outp, inp) => {
        outp.mainNonBurstInfo := inp.mainNonBurstInfo
      }
    )

    //myFrontDbusSlicer.io.host.h2dBus << cpu.io.lcvDbus.h2dBus
    //cpu.io.lcvDbus.d2hBus <-/< myFrontDbusSlicer.io.host.d2hBus
    myDbgDbusSlicer.io.host.h2dBus <-/< cpu.io.lcvDbus.h2dBus
    cpu.io.lcvDbus.d2hBus <-/< myDbgDbusSlicer.io.host.d2hBus

    //myBackDbusSlicer.io.host.h2dBus <-/< (
    //  myFrontDbusSlicer.io.devVec(
    //    cfg.myOtherFrontSliceVal
    //  ).h2dBus
    //)

    //myFrontDbusSlicer.io.devVec(
    //  cfg.myOtherFrontSliceVal
    //).d2hBus <-/< (
    //  myBackDbusSlicer.io.host.d2hBus
    //)

    //myBackDbusSlicer.io.devVec(3).h2dBus.ready := False
    //myBackDbusSlicer.io.devVec(3).d2hBus.valid := False
    //myBackDbusSlicer.io.devVec(3).d2hBus.payload := (
    //  myBackDbusSlicer.io.devVec(3).d2hBus.payload.getZero
    //)

    //myDbusSlicer.io.host.h2dBus <-/< cpu.io.lcvDbus.h2dBus
    //cpu.io.lcvDbus.d2hBus <-/< myDbusSlicer.io.host.d2hBus

    //--------
    // BEGIN: later `myDcache`
    val myDcache = (
      LcvBusCache(cfg=cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg),

      //LcvBusCache(cfg=LcvBusCacheBusPairConfig(
      //  mainCfg=cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg.mainCfg,
      //  loBusCacheCfg=LcvBusCacheConfig(
      //    kind=LcvCacheKind.D,
      //    lineSizeBytes=64,
      //    depthWords=(
      //      //4 * 1024 / (4 * 2)
      //      //256
      //      //64
      //      //128
      //      512 // 2 kiB data cache
      //    ).toInt,
      //    numCpus=1,
      //    lineWordMemRamStyleAltera=(
      //      "no_rw_check, M10K"
      //    ),
      //    lineAttrsMemRamStyleAltera=(
      //      //"no_rw_check, MLAB"
      //      "no_rw_check, M10K"
      //    ),
      //  ),
      //  hiBusCacheCfg=None,
      //))
    )
    myDcache.io.loBus << mySlicedNonFbDcacheHost
    mySdramCtrlDcacheHost <-/< myDcache.io.hiBus


    //mySlicedDcacheHost.h2dBus.translateInto(
    //  mySdramCtrlDcacheHost.h2dBus
    //)(
    //  dataAssignment=(outp, inp) => {
    //    outp.mainNonBurstInfo := inp.mainNonBurstInfo
    //    outp.mainBurstInfo := outp.mainBurstInfo.getZero
    //  }
    //)
    //mySdramCtrlDcacheHost.d2hBus.translateInto(
    //  mySlicedDcacheHost.d2hBus
    //)(
    //  dataAssignment=(outp, inp) => {
    //    outp.mainNonBurstInfo := inp.mainNonBurstInfo
    //    //outp.mainBurstInfo := outp.mainBurstInfo.getZero
    //  }
    //)

    //val myDbgNonFbDcacheByteEnAdapter = (
    //  LcvBusSlowNonBurstingByteEnAdapter(
    //    cfg=LcvBusByteEnAdapterConfig(
    //      loBusCfg=cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg.loBusCfg
    //    )
    //  )
    //)
    //myDbgNonFbDcacheByteEnAdapter.io.loBus.h2dBus << (
    //  mySlicedDcacheHost.h2dBus
    //)
    //mySlicedDcacheHost.d2hBus << (
    //  myDbgNonFbDcacheByteEnAdapter.io.loBus.d2hBus
    //)
    //myDbgNonFbDcacheByteEnAdapter.io.hiBus.h2dBus.translateInto(
    //  mySdramCtrlDcacheHost.h2dBus
    //)(
    //  dataAssignment=(outp, inp) => {
    //    outp.mainNonBurstInfo := inp.mainNonBurstInfo
    //    outp.mainBurstInfo := outp.mainBurstInfo.getZero
    //  }
    //)
    //mySdramCtrlDcacheHost.d2hBus.translateInto(
    //  myDbgNonFbDcacheByteEnAdapter.io.hiBus.d2hBus
    //)(
    //  dataAssignment=(outp, inp) => {
    //    outp.mainNonBurstInfo := inp.mainNonBurstInfo
    //    //outp.mainBurstInfo := outp.mainBurstInfo.getZero
    //  }
    //)

    // END: later `myDcache`
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

    //irqCtrl.io.bus <-/< mySlicedIoIrqCtrlHost
    mySlicedIoIrqCtrlHost.h2dBus.ready := False
    mySlicedIoIrqCtrlHost.d2hBus.valid := False
    mySlicedIoIrqCtrlHost.d2hBus.payload := (
      mySlicedIoIrqCtrlHost.d2hBus.payload.getZero
    )

    val nonIrqMmio = MeltedMoonNonIrqMmio(cfg=cfg)
    nonIrqMmio.io.bus <-/< mySlicedIoNonIrqMmioHost
    io.outpChar := nonIrqMmio.io.outpChar
  }
  //--------
  val ioctlArea = (
    !cfg.dbgUseLcvBusMem
  ) generate
    new Area
  {
    io.ioctl.upload_req := False
    io.ioctl.upload_index := 0x0
    io.ioctl.din := 0x0
    //io.ioctl.myWait := False
    case class MyIoctlPayload(
      dataWidth: Int,
    ) extends Bundle {
      val addr = UInt(cfg.sdramCtrlCfg.busCfg.addrWidth bits)
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
        //cfg.sdramCtrlCfg.busCfg.dataWidth
        /// 
        cfg.ioctlSpinalDw >> 3
      ).toInt
      //1
    )
    myIoctlRecvPushStm(1) <-/< myIoctlRecvPushStm.head
    myIoctlRecvPushStm(1).translateInto(myIoctlRecvPushStm(2))(
      dataAssignment=(outp, inp) => {
        outp.addr := (
          RegNext(
            outp.addr,
            init=outp.addr.getZero
          )
        )
        outp.data := (
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
                outp.isWrite := False//inp.isWrite//True//False
                outp.byteEn := 0x0//0x3
              } else {
                outp.addr := inp.addr //- 2
                outp.isWrite := inp.isWrite//True//inp.isWrite
                outp.byteEn := 0xf//0xc
              }
            }
          }
        }
      }
    )
    //myIoctlRecvPushStm.last <-< myIoctlRecvPushStm(2)
    myIoctlRecvPushStm.last << myIoctlRecvPushStm(2)

    myIoctlRecvPushStm.head.valid := (
      cartDownload
    )
    myIoctlRecvPushStm.head.data := (
      Mux(
        cartDownload && io.ioctl.wr,
        io.ioctl.dout,
        (
          RegNext(myIoctlRecvPushStm.head.data)
          init(0x0)
        )
      )
    )
    myIoctlRecvPushStm.head.addr := (
      Mux(
        cartDownload && io.ioctl.wr,
        io.ioctl.addr.resize(myIoctlRecvPushStm.head.addr.getWidth),
        (
          RegNext(myIoctlRecvPushStm.head.addr)
          init(0x0)
        )
      )
    )
    myIoctlRecvPushStm.head.isWrite := (
      True
    )
    io.ioctl.myWait := (
      cartDownload
      && !myIoctlRecvPushStm.head.ready
    )

    val myIoctlRecvFifo = StreamFifo(
      dataType=MyIoctlPayload(cfg.sdramCtrlCfg.busCfg.dataWidth),
      depth=8,
      latency=2,
      forFMax=true,
    )
    myIoctlRecvFifo.io.push <-/< myIoctlRecvPushStm.last
    // AH HA! This `.throwWhen` caused quite a few problems!
    //.throwWhen(
    //  (
    //    myIoctlRecvPushStm.last.addr(1)
    //    === RegNextWhen(
    //      myIoctlRecvPushStm.last.addr(1),
    //      cond=myIoctlRecvPushStm.last.fire,
    //      init=True,
    //    )
    //  )
    //)
    val myIoctlRecvPopStm = Stream(
      MyIoctlPayload(cfg.sdramCtrlCfg.busCfg.dataWidth)
    )
    myIoctlRecvPopStm <-/< myIoctlRecvFifo.io.pop//myIoctlRecvPushStm.last
    //myIoctlRecvPopStm << myIoctlRecvPushStm.last//myIoctlRecvFifo.io.pop
    myIoctlRecvPopStm.translateInto(mySdramCtrlIoctlFinalHost.h2dBus)(
      dataAssignment=(outp, inp) => {
        outp.addr := (
          Cat(
            inp.addr(inp.addr.high downto 2),
            U"2'b00",
          ).asUInt
        )
        outp.data := inp.data
        outp.isWrite := inp.isWrite
        outp.byteEn := inp.byteEn
        outp.src := outp.src.getZero

        outp.burstCnt := outp.burstCnt.getZero
        outp.burstFirst := False
        outp.burstLast := False
      }
    )
    mySdramCtrlIoctlFinalHost.d2hBus.ready := True
  }
  val rSeenIoctlDownloadEtc = (
    !cfg.dbgUseLcvBusMem
  ) generate (
    Reg(Bool(), init=False)
  )
  if (!cfg.dbgUseLcvBusMem) {
    when (
      //fell(io.ioctl.download)
      io.ioctl.download
    ) {
      rSeenIoctlDownloadEtc := True
    }
  }
  when (
    //fell(io.ioctl.download)
    if (!cfg.dbgUseLcvBusMem) (
      History[Bool](
        that=(
          rSeenIoctlDownloadEtc
          //&& fell(mySdramCtrlIoctlFinalHost.h2dBus.valid)
          //fell(io.ioctl.download)
        ),
        length=7,
        init=False
      ).last
    ) else (
      True
    )
  ) {
    rMyCpuResetState := False
  }
  //--------
}

case class MeltedMoonForSim(
  cfg: MeltedMoonConfig
) extends Component {
  //--------
  val io = MeltedMoonIo(cfg=cfg)
  noIoPrefix()
  //--------
  val cartDownload = (
    (
      //(
      //  //(
      //  //  (~io.ioctl.index(5 downto 0).orR)
      //  //  && (io.ioctl.index(7 downto 6) === 0x0)
      //  //)
      //  //||
      //  (
      //    io.ioctl.index(5 downto 0) === 0x1
      //  )
      //)
      //&&
      if (!cfg.dbgUseLcvBusMem) (
        io.ioctl.download
      ) else (
        False
      )
    )
    //|| !io.pllLocked
  )
  val myHistTempSoftReset = (
    Vec.fill(4)(
      History[Vec[Bool]](
        that=(io.softReset),
        length=(
          //2
          //3
          //4
          7
          //1
        ),
        init=(
          //False
          Vec.fill(2)(
            False
          )
        ),
      )//.last
    )
  )
  def myTempSoftReset(idx: Int) = myHistTempSoftReset(idx).last
  //val myTempSoftReset = myHistTempSoftReset.last
  //val myTempSdramCtrlSoftReset = (
  //  rose(myTempSoftReset)
  //  //myTempSoftReset
  //)
  val myTestArea =
    //new Area
    //new ResetArea(
    //  myTempSdramCtrlSoftReset,
    //  cumulative=true//false//true
    //)
    //--------
    // TODO: try `new Area` here...
    //--------
  (
    !cfg.dbgUseLcvBusMem
  ) generate new Area
  {
    val mySdramCtrl = (
      LcvBusSdramCtrl(
        cfg=cfg.sdramCtrlCfg
      )
    )
    mySdramCtrl.io.sdram <> io.sdram
    //mySdramCtrl.io.bus <-/< mySdramCtrlSoftReset.io.hiBus
  }

  def mySdramCtrl = myTestArea.mySdramCtrl
  //--------
  def mySdramCtrlFinalHostIdxIoctl = 0
  def mySdramCtrlFinalHostIdxInternal = 1
  def limMySdramCtrlFinalHostIdx = 2

  //val myTempSdramCtrlFinalBusArbiterSoftReset = (
  //  //rose(myTempSoftReset)
  //  //myTempSoftReset
  //  rose(myHistTempSoftReset(myHistTempSoftReset.size - 2))
  //)

  //val mySdramCtrlFinalBusArbiterArea =
  //  new Area
  //  //new ResetArea(
  //  //  myTempSdramCtrlFinalBusArbiterSoftReset,
  //  //  cumulative=true,
  //  //)
  //{
  //  val arbiter = LcvBusArbiter(
  //    cfg=LcvBusArbiterConfig(
  //      busCfg=cfg.sdramCtrlCfg.busCfg,
  //      numHosts=limMySdramCtrlFinalHostIdx,
  //      kind=(
  //        LcvBusArbiterKind.Priority
  //        //LcvBusArbiterKind.RoundRobin
  //      ),
  //    )
  //  )
  //  //val myInternalSoftReset = (
  //  //  LcvBusDevSoftReset(
  //  //    cfg=LcvBusDevSoftResetConfig(
  //  //      busCfg=cfg.sdramCtrlCfg.busCfg
  //  //    )
  //  //  )
  //  //)

  //  //val rInternalSoftResetState = Reg(Bool(), init=False)

  //  //when (myTempSdramCtrlFinalBusArbiterSoftReset) {
  //  //  rInternalSoftResetState := True
  //  //}
  //  //when (myInternalSoftReset.io.softResetDone) {
  //  //  rInternalSoftResetState := False
  //  //}

  //  arbiter.io.en := True
  //  arbiter.io.forceHost.valid := (
  //    False
  //    //myTempSdramCtrlFinalBusArbiterSoftReset
  //    //|| rInternalSoftResetState
  //    //myInternalSoftReset.io.softResetDone
  //  )
  //  arbiter.io.forceHost.payload := (
  //    0x0
  //    //mySdramCtrlFinalHostIdxIoctl
  //  )

  //  mySdramCtrl.io.bus <-/< arbiter.io.dev
  //  //arbiter.io.hostVec(mySdramCtrlFinalHostIdxInternal) <-/< (
  //  //  myInternalSoftReset.io.hiBus
  //  //)
  //  //myInternalSoftReset.io.softReset := (
  //  //  myTempSdramCtrlFinalBusArbiterSoftReset
  //  //)
  //}
  //def mySdramCtrlFinalBusArbiter = (
  //  mySdramCtrlFinalBusArbiterArea.arbiter
  //)
  //def mySdramCtrlIoctlFinalHost = (
  //  mySdramCtrlFinalBusArbiter.io.hostVec(mySdramCtrlFinalHostIdxIoctl)
  //)
  //def mySdramCtrlInternalFinalHost = (
  //  mySdramCtrlFinalBusArbiter.io.hostVec(mySdramCtrlFinalHostIdxInternal)
  //  //mySdramCtrlFinalBusArbiterArea.myInternalSoftReset.io.loBus
  //)
  //--------
  def mySdramCtrlHostIdxFbDcache = 0//1//0
  def mySdramCtrlHostIdxFbInit = 1//2//1
  def mySdramCtrlHostIdxIoctl = 2//0//2
  def mySdramCtrlHostIdxIcache = 4//3//2
  def mySdramCtrlHostIdxNonFbDcache = 3//4//3
  def limMySdramCtrlHostIdx = 5//4

  //val myTempSdramCtrlBusArbiterSoftReset = (
  //  rose(myTempSoftReset)
  //  //myTempSoftReset
  //  //rose(myHistTempSoftReset(myHistTempSoftReset.size - 2))
  //)
  val mySdramCtrlBusArbiterArea =
    new Area
    //new ResetArea(
    //  myTempSdramCtrlBusArbiterSoftReset,
    //  cumulative=true,
    //)
  {
    val arbiter = LcvBusArbiter(
      cfg=LcvBusArbiterConfig(
        busCfg=cfg.sdramCtrlCfg.busCfg,
        numHosts=limMySdramCtrlHostIdx,
        kind=(
          LcvBusArbiterKind.Priority
          //LcvBusArbiterKind.RoundRobin
        ),
      )
    )
    arbiter.io.en := True
    arbiter.io.forceHost.valid := False
    arbiter.io.forceHost.payload := 0

    val myDbgLcvBusMem = (
      cfg.dbgUseLcvBusMem
    ) generate {
      //val depth = 1 << (27 - 2) // 128 MiB
      val depth = 1 << (26 - 2) // 64 MiB

      val program = SnowHouseRam32InitFromBin(
        filename=cfg.cpuCfg.programStr
      )

      //val myMemDepth = 0x4000
      val myMemInitBigInt = {
        //val depth = myMemDepth
        val tempArr = new ArrayBuffer[BigInt]()
        tempArr ++= program.view
        while (tempArr.size < depth) {
          tempArr += BigInt(0)
        }
        tempArr
      }

      //val myInitBigInt = {
      //  //val depth = 1 << (16 - 4)

      //  val tempArr = new ArrayBuffer[BigInt]()
      //  tempArr ++= cfg.program.outpArr.view
      //  while (tempArr.size < depth) {
      //    tempArr += BigInt(0)
      //  }
      //  //val programSize = tempArr.size
      //  //for (idx <- programSize until (1 << (16 - 4))) {
      //  //  if (idx < /*1024*/0x800) {
      //  //    //println(
      //  //    //  s"idx < 0x800: ${idx}"
      //  //    //)
      //  //    //tempArr += BigInt(idx)
      //  //    tempArr += BigInt(0)
      //  //  } else {
      //  //    //println(
      //  //    //  s"idx < 0x800: ${idx}"
      //  //    //)
      //  //    //tempArr += BigInt(0)
      //  //  }
      //  //  //tempArr += BigInt(0)
      //  //}
      //  tempArr
      //  //for (elem <- program.outpArr.view) {
      //  //  tempArr +=
      //  //}
      //  //program.outpArr
      //}
      LcvBusMem(
        cfg=LcvBusMemConfig(
          busCfg=cfg.sdramCtrlCfg.busCfg,
          depth=depth,
          initBigInt=Some(myMemInitBigInt),
        )
      )
    }
    if (!cfg.dbgUseLcvBusMem) {
      mySdramCtrl.io.bus <-/< arbiter.io.dev
    } else {
      myDbgLcvBusMem.io.bus <-/< arbiter.io.dev
    }

    //mySdramCtrlInternalFinalHost <-/< arbiter.io.dev
  }

  //val mySoftResetCntMaxVal = ((1.0 ms) * cfg.clkRate).toLong
  //val rMySoftResetCnt = (
  //  Vec.fill(myHistTempSoftReset.size)(
  //    Reg(SInt((log2Up(mySoftResetCntMaxVal + 1) + 1) bits))
  //    init(mySoftResetCntMaxVal)
  //  )
  //)
  //for (idx <- 0 until rMySoftResetCnt.size) {
  //  when (
  //    //rose(
  //      myTempSoftReset(idx)
  //    //)
  //  ) {
  //    rMySoftResetCnt(idx) := mySoftResetCntMaxVal
  //  } elsewhen (!rMySoftResetCnt(idx).msb) {
  //    rMySoftResetCnt(idx) := rMySoftResetCnt(idx) - 1
  //  }
  //}

  def mkMyCpuAreaSoftReset(idx: Int, jdx: Int) = (
    //rose(myTempSoftReset)
    myTempSoftReset(idx=idx)(jdx)
    //|| 
    //!rMySoftResetCnt(idx).msb
    //|| 
    //(
    //  RegNextWhen(
    //    False,
    //    cond=(
    //      //io.softReset
    //      //fell(myTempSoftReset(idx=idx))
    //      myTempSoftReset(idx=idx)(jdx)
    //      //!rMySoftResetCnt(idx).msb
    //    ),
    //    init=True
    //  )
    //)
  )

  //val myCpuFbDcacheSoftReset = (
  //  LcvBusDevSoftReset(
  //    cfg=LcvBusDevSoftResetConfig(
  //      //busCfg=cfg.sdramCtrlCfg.busCfg
  //      busCfg=(
  //        cfg.myFbCtrlMmapCfg.busCfg
  //      ),
  //    )
  //  )
  //)
  //myCpuFbDcacheSoftReset.io.softReset := mkMyCpuAreaSoftReset(1)

  //val myCpuIcacheSoftReset = (
  //  LcvBusDevSoftReset(
  //    cfg=LcvBusDevSoftResetConfig(
  //      busCfg=cfg.sdramCtrlCfg.busCfg
  //    )
  //  )
  //)
  //myCpuIcacheSoftReset.io.softReset := mkMyCpuAreaSoftReset(3)

  //val myCpuNonFbDcacheSoftReset = (
  //  LcvBusDevSoftReset(
  //    cfg=LcvBusDevSoftResetConfig(
  //      busCfg=cfg.sdramCtrlCfg.busCfg
  //    )
  //  )
  //)
  //myCpuNonFbDcacheSoftReset.io.softReset := mkMyCpuAreaSoftReset(2)


  def mySdramCtrlBusArbiter = (
    mySdramCtrlBusArbiterArea.arbiter
  )
  def mySdramCtrlFbDcacheHost = (
    mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxFbDcache)
  )
  def mySdramCtrlFbInitHost = (
    mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxFbInit)
  )
  def mySdramCtrlIoctlFinalHost = (
    //mySdramCtrlFinalBusArbiter.io.hostVec(mySdramCtrlFinalHostIdxIoctl)
    mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxIoctl)
  )
  if (cfg.dbgUseLcvBusMem) {
    mySdramCtrlIoctlFinalHost.h2dBus.valid := False
    mySdramCtrlIoctlFinalHost.h2dBus.payload := (
      mySdramCtrlIoctlFinalHost.h2dBus.payload.getZero
    )
    mySdramCtrlIoctlFinalHost.d2hBus.ready := False
  }
  def mySdramCtrlIcacheHost = (
    mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxIcache)
    //myCpuIcacheSoftReset.io.loBus
  )
  def mySdramCtrlNonFbDcacheHost = (
    mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxNonFbDcache)
    //myCpuNonFbDcacheSoftReset.io.loBus
  )

  //mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxIcache) <-/< (
  //  myCpuIcacheSoftReset.io.hiBus
  //)
  //mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxNonFbDcache) <-/< (
  //  myCpuNonFbDcacheSoftReset.io.hiBus
  //)

  //--------
  //val myTempVgaSoftReset = (
  //  rose(myTempSoftReset(idx=0))
  //  //rose(myHistTempSoftReset(myHistTempSoftReset.size - 2))
  //)
  //val vgaClkDomain = ClockDomain.external(
  //  name="vgaClk",
  //  config=ClockDomainConfig(
  //    resetKind=SYNC,
  //  ),
  //  withReset=true,
  //  withSoftReset=false,
  //  withClockEnable=false,
  //  frequency=FixedFrequency(cfg.mainVgaClkRate),
  //)
  val vgaPushInpStm = Stream(Rgb(cfg.myFbCfg.rgbCfg))
  val vgaPushOutpStm = (
    Stream(Rgb(cfg.myFbCfg.rgbCfg))
    //StreamCCByToggle(
    //  input=vgaPushInpStm,
    //  inputClock=ClockDomain.current,
    //  outputClock=vgaClkDomain,
    //)
  )
  vgaPushOutpStm <-/< vgaPushInpStm
  //val vgaPushFifo = StreamFifoCC(
  //  dataType=Rgb(cfg.myFbCfg.rgbCfg),
  //  depth=(
  //    //32
  //    4
  //  ),
  //  pushClock=ClockDomain.current,
  //  popClock=vgaClkDomain,
  //)
  val vgaArea =
    new Area
    //new ResetArea(
    //  myTempVgaReset,
    //  cumulative=true,
    //)
    //new ClockingArea(
    //  clockDomain=vgaClkDomain
    //)
  {
    val vgaTimingInfo = cfg.vgaTimingInfo
    //val vgaCtrl = VgaCtrl(rgbConfig=cfg.rgbCfg)
    val lcvVgaCtrl = (
      LcvVgaCtrl(
        clkRate=cfg.mainVgaClkRate,
        rgbConfig=cfg.rgbCfg,
        vgaTimingInfo=vgaTimingInfo,
        fifoDepth=(
          1
        ),
      )
    )
    lcvVgaCtrl.io.fifoFlush := False
    //vgaCtrl.io.softReset := False//myTempVgaSoftReset

    //vgaCtrl.io.timings.setAs_h640_v480_r60
    //vgaTimingInfo.driveSpinalVgaTimings(
    //  clkRate=cfg.clkRate,
    //  spinalVgaTimings=vgaCtrl.io.timings
    //)
    lcvVgaCtrl.io.push <-/< vgaPushOutpStm//vgaPushFifo.io.pop
    //vgaCtrl.io.push <-/< vgaPushFifo.io.pop

    //vgaArea.vgaCtrl.io.pixels <-/< vgaPushFifo.io.pop
    val rVgaPhys = (
      Reg(cloneOf(io.vgaPhys), init=io.vgaPhys.getZero)
    )
    val rVgaVisib = (
      Reg(cloneOf(io.vgaVisib), init=io.vgaVisib.getZero)
    )
    val rVgaPixelEn = (
      Reg(cloneOf(io.vgaPixelEn), init=io.vgaPixelEn.getZero)
    )

    //io.vgaPhys.setAsReg() init(io.vgaPhys.getZero)
    //io.vgaVisib.setAsReg() init(io.vgaVisib.getZero)
    //io.vgaPixelEn.setAsReg() init(io.vgaPixelEn.getZero)
    val cpp = LcvVgaCtrl.cpp(
      clkRate=cfg.mainVgaClkRate,
      vgaTimingInfo=vgaTimingInfo
    )
    val rPixelEnCnt = (
      Reg(UInt(
        LcvVgaCtrl.clkCntWidth(
          clkRate=cfg.mainVgaClkRate,
          vgaTimingInfo=vgaTimingInfo
        ) bits
      ))
      init(0x0)
    )
    when (rPixelEnCnt.resize(rPixelEnCnt.getWidth + 1) + 1 < cpp) {
      rPixelEnCnt := rPixelEnCnt + 1
    } otherwise {
      rPixelEnCnt := 0x0
    }

    rVgaPhys.hsync := (
      lcvVgaCtrl.io.phys.hsync
      //vgaCtrl.rVga.hSync
    )
    rVgaPhys.vsync := (
      lcvVgaCtrl.io.phys.vsync
      //vgaCtrl.rVga.vSync
    )
    rVgaVisib := (
      lcvVgaCtrl.io.misc.visib
      //vgaCtrl.rVga.colorEn
    )
    rVgaPixelEn := (
      lcvVgaCtrl.io.misc.pixelEn
      //rPixelEnCnt === cpp - 1
    )

    lcvVgaCtrl.io.en := True
    when (
      lcvVgaCtrl.io.misc.visib
      //vgaCtrl.rVga.colorEn
    ) {
      rVgaPhys.col.r(2 downto 0) := 0x7
      rVgaPhys.col.r(7 downto 3) := (
        lcvVgaCtrl.io.phys.col.r
        //vgaCtrl.rVga.color.r
      )
      rVgaPhys.col.g(2 downto 0) := 0x7
      rVgaPhys.col.g(7 downto 3) := (
        lcvVgaCtrl.io.phys.col.g
        //vgaCtrl.rVga.color.g
      )
      rVgaPhys.col.b(2 downto 0) := 0x7
      rVgaPhys.col.b(7 downto 3) := (
        lcvVgaCtrl.io.phys.col.b
        //vgaCtrl.rVga.color.b
      )
    } otherwise {
      rVgaPhys.col := rVgaPhys.col.getZero
    }

    val myDoVblankIrq = Bool()
    myDoVblankIrq := (
      rose(
        RegNext(
          (lcvVgaCtrl.io.misc.vpipeS === LcvVgaState.front),
          //vgaCtrl.io.frameStart,
          init=False,
        )
      )
    )
  }

  //io.vgaPhys.setAsReg() init(io.vgaPhys.getZero)
  //io.vgaVisib.setAsReg() init(io.vgaVisib.getZero)
  //io.vgaPixelEn.setAsReg() init(io.vgaPixelEn.getZero)
  io.vgaPhys := vgaArea.rVgaPhys
  io.vgaVisib := vgaArea.rVgaVisib
  io.vgaPixelEn := vgaArea.rVgaPixelEn
  //--------
  //val myTempFbInitSoftReset = (
  //  rose(myHistTempSoftReset(myHistTempSoftReset.size - 2))
  //)
  val fbInitArea =
    new Area
    //new ResetArea(
    //  myTempFbInitSoftReset,
    //  cumulative=true
    //)
  {
    val vgaTimingInfo = cfg.vgaTimingInfo
    val fbSize2d = cfg.myFbCfg.fbSize2d

    //val mySdramSizeBytes = 1 << cfg.myDbusSlicerAddrSliceHi
    //val mySdramSizeWords = mySdramSizeBytes >> 2
    //val rCnt = (
    //  //Reg(UInt(log2Up(fbSize2d.y * fbSize2d.x + 1) + 1 bits))
    //  Reg(UInt(log2Up(mySdramSizeWords) + 1 bits))
    //  init(0x0)
    //)
    val myTempH2dStm = cloneOf(mySdramCtrlFbInitHost.h2dBus)
    mySdramCtrlFbInitHost.h2dBus <-/< myTempH2dStm
    mySdramCtrlFbInitHost.d2hBus.ready := True


    myTempH2dStm.valid := (
      //(rCnt < ((fbSize2d.y * fbSize2d.x) >> 1))
      //rCnt < mySdramSizeWords
      False
    )
    myTempH2dStm.addr := 0x0
    myTempH2dStm.addr.allowOverride
    ////myTempH2dStm.addr(cfg.fbAddrSliceHi) := True
    //myTempH2dStm.addr(rCnt.high + 2 downto 2) := rCnt
    myTempH2dStm.byteEn := (
      U(myTempH2dStm.byteEn.getWidth bits, default -> True)
    )
    myTempH2dStm.data := (
      0x0
      //U(
      //  myTempH2dStm.data.getWidth bits,
      //  // two blank pixels
      //  31 -> False,
      //  15 -> False,
      //  default -> //True//False//True
      //)
    )
    myTempH2dStm.src := 0x0
    myTempH2dStm.isWrite := True
    //when (myTempH2dStm.fire) {
    //  rCnt := rCnt + 1
    //}
    //when (
    //  //rose(io.softReset)
    //  rose(myTempSoftReset)
    //) {
    //  rCnt := 0x0
    //} elsewhen (myTempH2dStm.fire) {
    //  rCnt := rCnt + 1
    //}
    myTempH2dStm.burstFirst := (
      myTempH2dStm.addr(5 downto 2) === 0x0
    )
    myTempH2dStm.burstLast := (
      myTempH2dStm.addr(5 downto 2) === 0xf
    )
    myTempH2dStm.burstCnt := 15
  }
  //--------
  //val myTempFbAndDcacheSoftReset = (
  //  rose(myHistTempSoftReset(myHistTempSoftReset.size - 2))
  //)

  val fbAndDcacheArea = 
    new Area
    //new ResetArea(
    //  myTempFbAndDcacheSoftReset,
    //  cumulative=true,
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
            256
            //64
            //128
          ).toInt,
          numWays=(
            //1
            2
          ),
          numCpus=1,
          lineWordMemRamStyleAltera=(
            "no_rw_check, M10K"
          ),
          lineAttrsMemRamStyleAltera=(
            //"no_rw_check, MLAB"
            "no_rw_check, M10K"
          ),
        ),
        hiBusCacheCfg=None,
      ))
    )

    mySdramCtrlFbDcacheHost <-/< myFbDcache.io.hiBus
    //val myDbgFbDcacheByteEnAdapter = (
    //  LcvBusSlowNonBurstingByteEnAdapter(
    //    cfg=LcvBusByteEnAdapterConfig(
    //      loBusCfg=cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg.loBusCfg
    //    )
    //  )
    //)
    //myDbgFbDcacheByteEnAdapter.io.loBus.h2dBus << (
    //  mySdramCtrlFbDcacheHost.h2dBus
    //)
    //mySdramCtrlFbDcacheHost.d2hBus << (
    //  myDbgFbDcacheByteEnAdapter.io.loBus.d2hBus
    //)
    //myDbgFbDcacheByteEnAdapter.io.hiBus.h2dBus.translateInto(
    //  mySdramCtrlFbDcacheHost.h2dBus
    //)(
    //  dataAssignment=(outp, inp) => {
    //    outp.mainNonBurstInfo := inp.mainNonBurstInfo
    //    outp.mainBurstInfo := outp.mainBurstInfo.getZero
    //  }
    //)
    //mySdramCtrlFbDcacheHost.d2hBus.translateInto(
    //  myDbgFbDcacheByteEnAdapter.io.hiBus.d2hBus
    //)(
    //  dataAssignment=(outp, inp) => {
    //    outp.mainNonBurstInfo := inp.mainNonBurstInfo
    //    //outp.mainBurstInfo := outp.mainBurstInfo.getZero
    //  }
    //)


    //--------
    def myFbArbiterHostIdxFbCtrl = 0
    def myFbArbiterHostIdxCpu = 1
    def limMyFbArbiterHostIdx = 2

    val myFbDeburster = LcvBusDeburster(
      cfg=LcvBusDebursterConfig(
        loBusCfg=cfg.myFbCtrlMmapCfg.busCfg
      )
    )

    val myFbArbTempArea =
      //new ResetArea(
      //  myTempFbAndDcacheSoftReset,
      //  cumulative=true,
      //)
      new Area
    {
      val arbiter = LcvBusArbiter(
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
      arbiter.io.en := True
      arbiter.io.forceHost.valid := False
      arbiter.io.forceHost.payload := 0
      myFbDeburster.io.loBus <-/< arbiter.io.dev
      myFbDcache.io.loBus <-/< myFbDeburster.io.hiBus
      //mySdramCtrlFbDcacheHost <-/< myFbDeburster.io.hiBus

      //myDbgFbDcacheByteEnAdapter.io.loBus.h2dBus << (
      //  arbiter.io.dev.h2dBus
      //)
      //arbiter.io.dev.d2hBus << (
      //  myDbgFbDcacheByteEnAdapter.io.loBus.d2hBus
      //)
      //myFbDeburster.io.hiBus.h2dBus.translateInto(
      //  myDbgFbDcacheByteEnAdapter.io.loBus.h2dBus
      //)(
      //  dataAssignment=(outp, inp) => {
      //    outp.mainNonBurstInfo := inp.mainNonBurstInfo
      //    //outp.mainBurstInfo := outp.mainBurstInfo.getZero
      //  }
      //)
      //myDbgFbDcacheByteEnAdapter.io.loBus.d2hBus.translateInto(
      //  myFbDeburster.io.hiBus.d2hBus
      //)(
      //  dataAssignment=(outp, inp) => {
      //    outp.mainNonBurstInfo := inp.mainNonBurstInfo
      //    //outp.mainBurstInfo := outp.mainBurstInfo.getZero
      //  }
      //)
    }

    def myFbArbiter = myFbArbTempArea.arbiter

    def myFbArbFbCtrlHost = (
      myFbArbTempArea.arbiter.io.hostVec(myFbArbiterHostIdxFbCtrl)
    )
    def myFbArbCpuHost = (
      myFbArbTempArea.arbiter.io.hostVec(myFbArbiterHostIdxCpu)
      //myCpuFbDcacheSoftReset.io.loBus
    )
    //myFbArbTempArea.arbiter.io.hostVec(myFbArbiterHostIdxCpu) <-/< (
    //  myCpuFbDcacheSoftReset.io.hiBus
    //)

    val myFbCtrl = LcvBusFramebufferCtrl(cfg=cfg.myFbCfg)
    //vgaArea.lcvVgaCtrl.io.push <-/< myFbCtrl.io.pop
    //vgaPushFifo.io.push <-/< myFbCtrl.io.pop
    vgaPushInpStm <-/< myFbCtrl.io.pop
    //vgaArea.vgaCtrl.io.pixels <-/< myFbCtrl.io.pop
    myFbArbFbCtrlHost <-/< myFbCtrl.io.bus
  }
  //--------

  val rMyCpuResetState = (
    Reg(Bool(),
      init=(
        //if (!cfg.dbgUseLcvBusMem) (
          True
        //) else (
        //  False
        //)
      )
    )
  )
  //val rSeenCpuAreaSoftReset = (
  //  //Vec.fill(2)(
  //    Reg(Bool(), init=False)
  //  //)
  //)
  //when (rMyCpuResetState) {
  //  when (!rSeenCpuAreaSoftReset) {
  //    when (mkMyCpuAreaSoftReset(0, 0)) {
  //      rSeenCpuAreaSoftReset := True
  //    }
  //  } otherwise { // when (rSeenCpuAreaSoftReset)
  //    when (mkMyCpuAreaSoftReset(0, 1)) {
  //      rMyCpuResetState := False
  //    }
  //  }
  //}

  //when (
  //  fell(io.ioctl.download)
  //) {
  //  rMyCpuResetState := False
  //}

  //switch (rMyCpuResetState) {
  //  is (False) {
  //  }
  //  is (True) {
  //  }
  //}
  //when (rSeenCpuAreaSoftReset(0)) {
  //}
  //when (mkMyCpuAreaSoftReset(0, 0)) {
  //  rMyCpuResetState := True
  //}
  //when (
  //  myTempSoftReset
  //  && mkMyCpuAreaSoftReset(0, 1)
  //) {
  //  rMyCpuResetState := False
  //}
  val cpuArea =
    new Area
    //new ResetArea(
    //  //myCpuAreaSoftReset,
    //  ////mkMyCpuAreaSoftReset(0),
    //  History[Bool](
    //    that=rMyCpuResetState,
    //    length=7,
    //    init=True,
    //  ).last,
    //  //fell(rMyCpuResetState),
    //  cumulative=(
    //    if (!cfg.dbgUseLcvBusMem) (
    //      false//true//false//true//false//true
    //    ) else (
    //      true
    //    )
    //  )
    //)
    //new ResetArea(
    //  //myCpuAreaSoftReset,
    //  ////mkMyCpuAreaSoftReset(0),
    //  History[Bool](
    //    that=rMyCpuResetState,
    //    length=7,
    //    init=True,
    //  ).last,
    //  //fell(rMyCpuResetState),
    //  cumulative=(
    //    if (!cfg.dbgUseLcvBusMem) (
    //      false//true//false//true//false//true//false//true
    //    ) else (
    //      true
    //    )
    //  )
    //)
  {
    //--------
    val myCpuInnerArea =
      //new Area
      //new ResetArea(
      //  //myCpuAreaSoftReset,
      //  mkMyCpuAreaSoftReset(0),
      //  cumulative=true//false//true
      //)
      new ResetArea(
        //myCpuAreaSoftReset,
        ////mkMyCpuAreaSoftReset(0),
        History[Bool](
          that=rMyCpuResetState,
          length=7,
          init=True,
        ).last,
        //fell(rMyCpuResetState),
        cumulative=(
          if (!cfg.dbgUseLcvBusMem) (
            false//true//false//true//false//true
          ) else (
            true
          )
        )
      )
    {
      val cpu = (
        //SnowHouseCpuWithoutRam(program=cfg.testProgram.program)
        SnowHouseRiscv32imWithoutRam(
          //program=cfg.testProgram.program
          cfg=cfg.cpuCfg
        )
      )
    }
    def cpu = myCpuInnerArea.cpu
    //--------
    //val irqCtrl = LcvBusIrqCtrl(
    //  cfg=LcvBusIrqCtrlConfig(
    //    busCfg=cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg.loBusCfg,
    //    depth=2,
    //  )
    //)

    //cpu.io.idsIraIrq.nextValid := (
    //  //!myCpuAreaSoftReset
    //  //&& 
    //  irqCtrl.io.dstIrq.nextValid
    //)
    //irqCtrl.io.dstIrq.ready := (
    //  //!myCpuAreaSoftReset
    //  //&& 
    //  cpu.io.idsIraIrq.ready
    //)

    //def myDoVblankIrq = vgaArea.myDoVblankIrq
    //irqCtrl.io.srcIrqVec(0) := (
    //  //!myCpuAreaSoftReset
    //  //&& 
    //  myDoVblankIrq
    //)

    //val myTimerIrqOverflow = (
    //  floor((cfg.clkRate / (1.0 kHz)).toDouble).toInt
    //)
    //val rTimerIrqCnt = (
    //  Reg(UInt(log2Up(myTimerIrqOverflow + 1) + 1 bits))
    //  init(0x0)
    //)
    //when (rTimerIrqCnt < myTimerIrqOverflow - 1) {
    //  rTimerIrqCnt := rTimerIrqCnt + 1
    //  irqCtrl.io.srcIrqVec(1) := False
    //} otherwise {
    //  rTimerIrqCnt := 0x0
    //  irqCtrl.io.srcIrqVec(1) := True//!myCpuAreaSoftReset //True
    //}
    //--------
    val icache = (
      LcvBusCache(cfg=cfg.cpuCfg.shCfg.subCfg.lcvIbusEtcCfg)
      //LcvBusCache(cfg=LcvBusCacheBusPairConfig(
      //  mainCfg=cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg.mainCfg,
      //  loBusCacheCfg=LcvBusCacheConfig(
      //    kind=LcvCacheKind.I,
      //    lineSizeBytes=64,
      //    depthWords=(
      //      //4 * 1024 / (4 * 2)
      //      //256
      //      //64
      //      //128
      //      512 // 2 kiB data cache
      //    ).toInt,
      //    numCpus=1,
      //    lineWordMemRamStyleAltera=(
      //      "no_rw_check, M10K"
      //    ),
      //    lineAttrsMemRamStyleAltera=(
      //      //"no_rw_check, MLAB"
      //      "no_rw_check, M10K"
      //    ),
      //  ),
      //  hiBusCacheCfg=None,
      //))
    )
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
        //val tempCond = (
        //  inp.src
        //  === (
        //    RegNextWhen(
        //      inp.src,
        //      cond=icache.io.loBus.d2hBus.fire,
        //    )
        //    init(0x2)
        //  )
        //)
        //val rState = Reg(Bool(), init=False)
        //when (
        //  !myHistCpuIbusD2hFire.last
        //  || (
        //    tempCond
        //    && !rState
        //  )
        //) {
        //  outp.data := outp.data.getZero
        //  when (inp.src === 0x3) {
        //    rState := True
        //  }
        //}
      }
    )
    cpu.io.lcvIbus.d2hBus <-/< myTempCpuLcvIbusD2hStm

    mySdramCtrlIcacheHost <-/< icache.io.hiBus

    //val myDbgIcacheByteEnAdapter = (
    //  LcvBusSlowNonBurstingByteEnAdapter(
    //    cfg=LcvBusByteEnAdapterConfig(
    //      loBusCfg=cfg.cpuCfg.shCfg.subCfg.lcvIbusEtcCfg.loBusCfg
    //    )
    //  )
    //)
    //myDbgIcacheByteEnAdapter.io.loBus.h2dBus << cpu.io.lcvIbus.h2dBus
    //cpu.io.lcvIbus.d2hBus <-/< myDbgIcacheByteEnAdapter.io.loBus.d2hBus

    //myDbgIcacheByteEnAdapter.io.hiBus.h2dBus.translateInto(
    //  mySdramCtrlIcacheHost.h2dBus
    //)(
    //  dataAssignment=(outp, inp) => {
    //    outp.mainNonBurstInfo := inp.mainNonBurstInfo
    //    outp.mainBurstInfo := outp.mainBurstInfo.getZero
    //  }
    //)
    //mySdramCtrlIcacheHost.d2hBus.translateInto(
    //  myDbgIcacheByteEnAdapter.io.hiBus.d2hBus
    //)(
    //  dataAssignment=(outp, inp) => {
    //    outp.mainNonBurstInfo := inp.mainNonBurstInfo
    //  }
    //)

    //--------
    val myDbgDbusSlicer = LcvBusSlicer(
      cfg=LcvBusSlicerConfig(
        mmapCfg=(
          //cfg.myFbDbusSlicerMmapCfg
          cfg.myDbgDbusSlicerMmapCfg
          //cfg.myFrontDbusSlicerMmapCfg
        ),
        maxNumOutstandingTxns=(
          // if this doesn't work, try increasing it.
          // It has been reduced to shrink the size of the counter for fmax
          // purposes
          4
        ),
      )
    )
    //val myFrontDbusSlicer = LcvBusSlicer(
    //  cfg=LcvBusSlicerConfig(
    //    mmapCfg=(
    //      //cfg.myFbDbusSlicerMmapCfg
    //      //cfg.myDbusSlicerMmapCfg
    //      cfg.myFrontDbusSlicerMmapCfg
    //    ),
    //    maxNumOutstandingTxns=(
    //      // if this doesn't work, try increasing it.
    //      // It has been reduced to shrink the size of the counter for fmax
    //      // purposes
    //      4
    //    ),
    //  )
    //)
    //val myBackDbusSlicer = LcvBusSlicer(
    //  cfg=LcvBusSlicerConfig(
    //    mmapCfg=cfg.myBackDbusSlicerMmapCfg,
    //    maxNumOutstandingTxns=(
    //      // if this doesn't work, try increasing it.
    //      // It has been reduced to shrink the size of the counter for fmax
    //      // purposes
    //      4
    //    ),
    //  )
    //)

    val myFbCpuHostClone = cloneOf(fbAndDcacheArea.myFbArbCpuHost)

    fbAndDcacheArea.myFbArbCpuHost <-/< myFbCpuHostClone

    def mySlicedNonFbDcacheHost = myDbgDbusSlicer.io.devVec(
      cfg.myNonFbSdramAddrDbgSliceVal
    )
    def mySlicedFbHost = myDbgDbusSlicer.io.devVec(
      cfg.myFbAddrDbgSliceVal
    )
    def mySlicedIoIrqCtrlHost = myDbgDbusSlicer.io.devVec(
      cfg.myIoIrqCtrlAddrDbgSliceVal
    )
    def mySlicedIoNonIrqMmioHost = myDbgDbusSlicer.io.devVec(
      cfg.myIoNonIrqMmioAddrDbgSliceVal
    )
    //def mySlicedNonFbDcacheHost = myFrontDbusSlicer.io.devVec(
    //  cfg.myNonFbSdramAddrFrontSliceVal
    //)
    //def mySlicedFbHost = myBackDbusSlicer.io.devVec(
    //  cfg.myFbAddrBackSliceVal
    //)
    //def mySlicedIoIrqCtrlHost = myBackDbusSlicer.io.devVec(
    //  cfg.myIoIrqCtrlAddrBackSliceVal
    //)
    //def mySlicedIoNonIrqMmioHost = myBackDbusSlicer.io.devVec(
    //  cfg.myIoNonIrqMmioAddrBackSliceVal
    //)

    mySlicedFbHost.h2dBus.translateInto(myFbCpuHostClone.h2dBus)(
      dataAssignment=(outp, inp) => {
        outp.mainNonBurstInfo := inp.mainNonBurstInfo
        outp.mainBurstInfo := outp.mainBurstInfo.getZero
      }
    )
    myFbCpuHostClone.d2hBus.translateInto(mySlicedFbHost.d2hBus)(
      dataAssignment=(outp, inp) => {
        outp.mainNonBurstInfo := inp.mainNonBurstInfo
      }
    )

    //myFrontDbusSlicer.io.host.h2dBus << cpu.io.lcvDbus.h2dBus
    //cpu.io.lcvDbus.d2hBus <-/< myFrontDbusSlicer.io.host.d2hBus
    myDbgDbusSlicer.io.host.h2dBus <-/< cpu.io.lcvDbus.h2dBus
    cpu.io.lcvDbus.d2hBus <-/< myDbgDbusSlicer.io.host.d2hBus

    //myBackDbusSlicer.io.host.h2dBus <-/< (
    //  myFrontDbusSlicer.io.devVec(
    //    cfg.myOtherFrontSliceVal
    //  ).h2dBus
    //)

    //myFrontDbusSlicer.io.devVec(
    //  cfg.myOtherFrontSliceVal
    //).d2hBus <-/< (
    //  myBackDbusSlicer.io.host.d2hBus
    //)

    //myBackDbusSlicer.io.devVec(3).h2dBus.ready := False
    //myBackDbusSlicer.io.devVec(3).d2hBus.valid := False
    //myBackDbusSlicer.io.devVec(3).d2hBus.payload := (
    //  myBackDbusSlicer.io.devVec(3).d2hBus.payload.getZero
    //)

    //myDbusSlicer.io.host.h2dBus <-/< cpu.io.lcvDbus.h2dBus
    //cpu.io.lcvDbus.d2hBus <-/< myDbusSlicer.io.host.d2hBus
    //--------
    // BEGIN: later `myNonFbDcache`
    val myNonFbDcache = (
      LcvBusCache(cfg=cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg),

      //LcvBusCache(cfg=LcvBusCacheBusPairConfig(
      //  mainCfg=cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg.mainCfg,
      //  loBusCacheCfg=LcvBusCacheConfig(
      //    kind=LcvCacheKind.D,
      //    lineSizeBytes=64,
      //    depthWords=(
      //      //4 * 1024 / (4 * 2)
      //      //256
      //      //64
      //      //128
      //      512 // 2 kiB data cache
      //    ).toInt,
      //    numCpus=1,
      //    lineWordMemRamStyleAltera=(
      //      "no_rw_check, M10K"
      //    ),
      //    lineAttrsMemRamStyleAltera=(
      //      //"no_rw_check, MLAB"
      //      "no_rw_check, M10K"
      //    ),
      //  ),
      //  hiBusCacheCfg=None,
      //))
    )
    myNonFbDcache.io.loBus << mySlicedNonFbDcacheHost
    mySdramCtrlNonFbDcacheHost <-/< myNonFbDcache.io.hiBus


    //mySlicedNonFbDcacheHost.h2dBus.translateInto(
    //  mySdramCtrlNonFbDcacheHost.h2dBus
    //)(
    //  dataAssignment=(outp, inp) => {
    //    outp.mainNonBurstInfo := inp.mainNonBurstInfo
    //    outp.mainBurstInfo := outp.mainBurstInfo.getZero
    //  }
    //)
    //mySdramCtrlNonFbDcacheHost.d2hBus.translateInto(
    //  mySlicedNonFbDcacheHost.d2hBus
    //)(
    //  dataAssignment=(outp, inp) => {
    //    outp.mainNonBurstInfo := inp.mainNonBurstInfo
    //    //outp.mainBurstInfo := outp.mainBurstInfo.getZero
    //  }
    //)

    //val myDbgNonFbDcacheByteEnAdapter = (
    //  LcvBusSlowNonBurstingByteEnAdapter(
    //    cfg=LcvBusByteEnAdapterConfig(
    //      loBusCfg=cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg.loBusCfg
    //    )
    //  )
    //)
    //myDbgNonFbDcacheByteEnAdapter.io.loBus.h2dBus << (
    //  mySlicedNonFbDcacheHost.h2dBus
    //)
    //mySlicedNonFbDcacheHost.d2hBus << (
    //  myDbgNonFbDcacheByteEnAdapter.io.loBus.d2hBus
    //)
    //myDbgNonFbDcacheByteEnAdapter.io.hiBus.h2dBus.translateInto(
    //  mySdramCtrlNonFbDcacheHost.h2dBus
    //)(
    //  dataAssignment=(outp, inp) => {
    //    outp.mainNonBurstInfo := inp.mainNonBurstInfo
    //    outp.mainBurstInfo := outp.mainBurstInfo.getZero
    //  }
    //)
    //mySdramCtrlNonFbDcacheHost.d2hBus.translateInto(
    //  myDbgNonFbDcacheByteEnAdapter.io.hiBus.d2hBus
    //)(
    //  dataAssignment=(outp, inp) => {
    //    outp.mainNonBurstInfo := inp.mainNonBurstInfo
    //    //outp.mainBurstInfo := outp.mainBurstInfo.getZero
    //  }
    //)

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

    //irqCtrl.io.bus <-/< mySlicedIoIrqCtrlHost
    mySlicedIoIrqCtrlHost.h2dBus.ready := False
    mySlicedIoIrqCtrlHost.d2hBus.valid := False
    mySlicedIoIrqCtrlHost.d2hBus.payload := (
      mySlicedIoIrqCtrlHost.d2hBus.payload.getZero
    )

    val nonIrqMmio = MeltedMoonNonIrqMmio(cfg=cfg)
    nonIrqMmio.io.bus <-/< mySlicedIoNonIrqMmioHost
    io.outpChar := nonIrqMmio.io.outpChar
  }
  //--------
  val ioctlArea = (
    !cfg.dbgUseLcvBusMem
  ) generate
    new Area
  {
    io.ioctl.upload_req := False
    io.ioctl.upload_index := 0x0
    io.ioctl.din := 0x0
    //io.ioctl.myWait := False
    case class MyIoctlPayload(
      dataWidth: Int,
    ) extends Bundle {
      val addr = UInt(cfg.sdramCtrlCfg.busCfg.addrWidth bits)
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
    myIoctlRecvPushStm(1) <-/< myIoctlRecvPushStm.head
    myIoctlRecvPushStm(1).translateInto(myIoctlRecvPushStm(2))(
      dataAssignment=(outp, inp) => {
        outp.addr := (
          RegNext(
            outp.addr,
            init=outp.addr.getZero
          )
        )
        outp.data := (
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
                outp.isWrite := False//inp.isWrite//True//False
                outp.byteEn := 0x0//0x3
              } else {
                outp.addr := inp.addr //- 2
                outp.isWrite := inp.isWrite//True//inp.isWrite
                outp.byteEn := 0xf//0xc
              }
            }
          }
        }
      }
    )
    //myIoctlRecvPushStm.last <-< myIoctlRecvPushStm(2)
    myIoctlRecvPushStm.last << myIoctlRecvPushStm(2)

    myIoctlRecvPushStm.head.valid := (
      cartDownload
    )
    myIoctlRecvPushStm.head.data := (
      Mux(
        cartDownload && io.ioctl.wr,
        io.ioctl.dout,
        (
          RegNext(myIoctlRecvPushStm.head.data)
          init(0x0)
        )
      )
    )
    myIoctlRecvPushStm.head.addr := (
      Mux(
        cartDownload && io.ioctl.wr,
        io.ioctl.addr.resize(myIoctlRecvPushStm.head.addr.getWidth),
        (
          RegNext(myIoctlRecvPushStm.head.addr)
          init(0x0)
        )
      )
    )
    myIoctlRecvPushStm.head.isWrite := (
      True
    )
    io.ioctl.myWait := (
      cartDownload
      && !myIoctlRecvPushStm.head.ready
    )

    val myIoctlRecvFifo = StreamFifo(
      dataType=MyIoctlPayload(cfg.sdramCtrlCfg.busCfg.dataWidth),
      depth=8,
      latency=2,
      forFMax=true,
    )
    myIoctlRecvFifo.io.push <-/< myIoctlRecvPushStm.last
    // AH HA! This `.throwWhen` caused quite a few problems!
    //.throwWhen(
    //  (
    //    myIoctlRecvPushStm.last.addr(1)
    //    === RegNextWhen(
    //      myIoctlRecvPushStm.last.addr(1),
    //      cond=myIoctlRecvPushStm.last.fire,
    //      init=True,
    //    )
    //  )
    //)
    val myIoctlRecvPopStm = Stream(
      MyIoctlPayload(cfg.sdramCtrlCfg.busCfg.dataWidth)
    )
    myIoctlRecvPopStm <-/< myIoctlRecvFifo.io.pop//myIoctlRecvPushStm.last
    //myIoctlRecvPopStm << myIoctlRecvPushStm.last//myIoctlRecvFifo.io.pop
    myIoctlRecvPopStm.translateInto(mySdramCtrlIoctlFinalHost.h2dBus)(
      dataAssignment=(outp, inp) => {
        outp.addr := (
          Cat(
            inp.addr(inp.addr.high downto 2),
            U"2'b00",
          ).asUInt
        )
        outp.data := inp.data
        outp.isWrite := inp.isWrite
        outp.byteEn := inp.byteEn
        outp.src := outp.src.getZero

        outp.burstCnt := outp.burstCnt.getZero
        outp.burstFirst := False
        outp.burstLast := False
      }
    )
    mySdramCtrlIoctlFinalHost.d2hBus.ready := True
  }
  val rSeenIoctlDownloadEtc = (
    !cfg.dbgUseLcvBusMem
  ) generate (
    Reg(Bool(), init=False)
  )
  if (!cfg.dbgUseLcvBusMem) {
    when (
      //fell(io.ioctl.download)
      io.ioctl.download
    ) {
      rSeenIoctlDownloadEtc := True
    }
  }
  when (
    //fell(io.ioctl.download)
    if (!cfg.dbgUseLcvBusMem) (
      History[Bool](
        that=(
          rSeenIoctlDownloadEtc
          //&& fell(mySdramCtrlIoctlFinalHost.h2dBus.valid)
          //fell(io.ioctl.download)
        ),
        length=7,
        init=False
      ).last
    ) else (
      True
    )
  ) {
    rMyCpuResetState := False
  }
  //--------
}

case class MeltedMoon(
  cfg: MeltedMoonConfig
) extends Component {
  val io = MeltedMoonIo(cfg=cfg)
  noIoPrefix()
  val mySimArea = (
    cfg.inSim
  ) generate (new Area {
    val meltedMoon = MeltedMoonForSim(cfg=cfg)
    io <> meltedMoon.io
  })
  val myFbDdramArea = (
    !cfg.inSim
  ) generate (new Area {
    val meltedMoon = MeltedMoonFbDdram(cfg=cfg)
    io <> meltedMoon.io
  })
}


//case class MeltedMoonOld3(
//  cfg: MeltedMoonConfig
//) extends Component {
//  //--------
//  val io = MeltedMoonIo(cfg=cfg)
//  noIoPrefix()
//  //--------
//  val cartDownload = (
//    (
//      (
//        //(
//        //  (~io.ioctl.index(5 downto 0).orR)
//        //  && (io.ioctl.index(7 downto 6) === 0x0)
//        //)
//        //||
//        (
//          io.ioctl.index(5 downto 0) === 0x1
//        )
//      )
//      && io.ioctl.download
//    )
//    //|| !io.pllLocked
//  )
//  val myTempSoftReset = (
//    History[Bool](
//      that=(
//        io.softReset
//      ),
//      length=(
//        //7
//        2
//      ),
//      init=False,
//    ).last//.asBits.asUInt.orR
//  )
//  //--------
//  val myTempSdramCtrlSoftReset = (
//    rose(myTempSoftReset)
//    //myTempSoftReset
//  )
//  val myTestArea =
//    //new Area
//    //new ResetArea(
//    //  myTempSdramCtrlSoftReset,
//    //  cumulative=true//false//true
//    //)
//    //--------
//    // TODO: try `new Area` here...
//    //--------
//    new Area
//  {
//    val mySdramCtrl = (
//      LcvBusSdramCtrl(
//        cfg=cfg.sdramCtrlCfg
//      )
//    )
//    mySdramCtrl.io.sdram <> io.sdram
//    //mySdramCtrl.io.softReset := rose(io.softReset)
//    val mySdramCtrlSoftReset = (
//      LcvBusDevSoftReset(
//        cfg=LcvBusDevSoftResetConfig(
//          busCfg=cfg.sdramCtrlCfg.busCfg
//        )
//      )
//    )
//    mySdramCtrl.io.bus <-/< mySdramCtrlSoftReset.io.hiBus
//    mySdramCtrlSoftReset.io.softReset := myTempSdramCtrlSoftReset
//  }
//  //def mySdramCtrl = myTestArea.mySdramCtrl
//  def mySdramCtrlSoftReset = myTestArea.mySdramCtrlSoftReset
//  //--------
//  def mySdramCtrlFinalHostIdxIoctl = 0
//  def mySdramCtrlFinalHostIdxInternal = 1
//  def limMySdramCtrlFinalHostIdx = 2
//
//  val myTempSdramCtrlFinalBusArbiterSoftReset = (
//    rose(myTempSoftReset)
//    //myTempSoftReset
//  )
//
//  val mySdramCtrlFinalBusArbiterArea =
//    new Area
//  {
//    val arbiter = 
//    LcvBusArbiter(
//      cfg=LcvBusArbiterConfig(
//        busCfg=cfg.sdramCtrlCfg.busCfg,
//        numHosts=limMySdramCtrlFinalHostIdx,
//        kind=(
//          LcvBusArbiterKind.Priority
//          //LcvBusArbiterKind.RoundRobin
//        ),
//      )
//    )
//    arbiter.io.en := True
//    arbiter.io.forceHost.valid := (
//      myTempSdramCtrlFinalBusArbiterSoftReset
//    )
//    arbiter.io.forceHost.payload := (
//      mySdramCtrlFinalHostIdxIoctl
//    )
//
//    //mySdramCtrl.io.bus <-/< arbiter.io.dev
//    mySdramCtrlSoftReset.io.loBus <-/< arbiter.io.dev
//  }
//  def mySdramCtrlFinalBusArbiter = (
//    mySdramCtrlFinalBusArbiterArea.arbiter
//  )
//  //--------
//  def mySdramCtrlHostIdxFbDcache = 0
//  def mySdramCtrlHostIdxFbInit = 1
//  def mySdramCtrlHostIdxIcache = 2
//  def mySdramCtrlHostIdxNonFbDcache = 3
//  def limMySdramCtrlHostIdx = 4
//
//  val myTempSdramCtrlBusArbiterSoftReset = (
//    rose(myTempSoftReset)
//    //myTempSoftReset
//  )
//  val mySdramCtrlBusArbiterArea =
//    new ResetArea(
//      myTempSdramCtrlBusArbiterSoftReset,
//      cumulative=true,
//    )
//    //new Area
//  {
//    val arbiter =
//    //val mySdramCtrlBusArbiter = 
//    LcvBusArbiter(
//      cfg=LcvBusArbiterConfig(
//        busCfg=cfg.sdramCtrlCfg.busCfg,
//        numHosts=limMySdramCtrlHostIdx, // add 1 for the icache
//        kind=(
//          LcvBusArbiterKind.Priority
//          //LcvBusArbiterKind.RoundRobin
//        ),
//      )
//    )
//    arbiter.io.en := (
//      True
//      //!myTempRstCondRisingEdge
//      //!myTempRstCondMain // `cpuArea`'s next reset signal
//    )
//    //arbiter.io.softReset := (
//    //  //rose(io.softReset)
//    //  //!myTempSoftReset
//    //  rose(myTempSoftReset)
//    //)
//    arbiter.io.forceHost.valid := (
//      //myTempSdramCtrlBusArbiterSoftReset
//      False
//      //Cat(
//      //  rose(myTempSoftReset),//False//rose(myTempSoftReset)
//      //  RegNext(rose(myTempSoftReset), init=False),
//      //  RegNext(RegNext(rose(myTempSoftReset)), init=False),
//      //  RegNext(RegNext(RegNext(rose(myTempSoftReset))), init=False),
//      //  RegNext(
//      //    RegNext(RegNext(RegNext(rose(myTempSoftReset)))),
//      //    init=False
//      //  ),
//      //).asUInt.orR
//    )
//    arbiter.io.forceHost.payload := (
//      //mySdramCtrlHostIdxIoctl
//      //mySdramCtrlHostIdxFbDcache
//      0
//    )
//
//    //mySdramCtrl.io.bus <-/< arbiter.io.dev
//    //mySdramCtrlSoftReset.io.loBus <-/< arbiter.io.dev
//    mySdramCtrlFinalBusArbiter.io.hostVec(
//      mySdramCtrlFinalHostIdxInternal
//    ) <-/< arbiter.io.dev
//  }
//  def mySdramCtrlBusArbiter = (
//    mySdramCtrlBusArbiterArea.arbiter
//  )
//
//  def mySdramCtrlFbDcacheHost = (
//    //sdramArea.mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxFbDcache)
//    mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxFbDcache)
//  )
//  def mySdramCtrlFbInitHost = (
//    //sdramArea.mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxFbInit)
//    mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxFbInit)
//  )
//  def mySdramCtrlIoctlFinalHost = (
//    //sdramArea.mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxIoctl)
//    //mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxIoctl)
//    mySdramCtrlFinalBusArbiter.io.hostVec(mySdramCtrlFinalHostIdxIoctl)
//  )
//  val mySdramCtrlIcacheHostSoftReset = (
//    LcvBusDevSoftReset(
//      cfg=LcvBusDevSoftResetConfig(
//        busCfg=cfg.sdramCtrlCfg.busCfg
//      )
//    )
//  )
//  mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxIcache) <-/< (
//    mySdramCtrlIcacheHostSoftReset.io.hiBus
//  )
//  def mySdramCtrlIcacheHost = (
//    //sdramArea.mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxIcache)
//    //mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxIcache)
//    mySdramCtrlIcacheHostSoftReset.io.loBus
//  )
//
//  val mySdramCtrlNonFbDcacheHostSoftReset = (
//    LcvBusDevSoftReset(
//      cfg=LcvBusDevSoftResetConfig(
//        busCfg=cfg.sdramCtrlCfg.busCfg
//      )
//    )
//  )
//  mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxNonFbDcache) <-/< (
//    mySdramCtrlNonFbDcacheHostSoftReset.io.hiBus
//  )
//  def mySdramCtrlNonFbDcacheHost = (
//    //sdramArea.mySdramCtrlBusArbiter.io.hostVec(
//    //  mySdramCtrlHostIdxNonFbDcache
//    //)
//    //mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxNonFbDcache)
//    mySdramCtrlNonFbDcacheHostSoftReset.io.loBus
//  )
//  //--------
//  val myTempVgaReset = (
//    //rose(myTempSoftReset)
//    rose(myTempSoftReset)
//    //myTempSoftReset
//  )
//
//  val vgaArea =
//    new ResetArea(
//      myTempVgaReset,
//      cumulative=true
//    )
//    //new Area
//  {
//    val vgaTimingInfo = cfg.vgaTimingInfo
//    val lcvVgaCtrl = (
//      LcvVgaCtrl(
//        clkRate=cfg.clkRate,
//        rgbConfig=cfg.rgbCfg,
//        vgaTimingInfo=vgaTimingInfo,
//        fifoDepth=(
//          1
//        ),
//      )
//    )
//    lcvVgaCtrl.io.fifoFlush := False
//    io.vgaPhys.setAsReg() init(io.vgaPhys.getZero)
//    io.vgaVisib.setAsReg() init(io.vgaVisib.getZero)
//    io.vgaPixelEn.setAsReg() init(io.vgaPixelEn.getZero)
//
//    io.vgaPhys.hsync := lcvVgaCtrl.io.phys.hsync
//    io.vgaPhys.vsync := lcvVgaCtrl.io.phys.vsync
//    io.vgaVisib := lcvVgaCtrl.io.misc.visib
//    io.vgaPixelEn := lcvVgaCtrl.io.misc.pixelEn
//
//    lcvVgaCtrl.io.en := True
//    when (lcvVgaCtrl.io.misc.visib) {
//      io.vgaPhys.col.r(2 downto 0) := 0x7
//      io.vgaPhys.col.r(7 downto 3) := lcvVgaCtrl.io.phys.col.r
//      io.vgaPhys.col.g(2 downto 0) := 0x7
//      io.vgaPhys.col.g(7 downto 3) := lcvVgaCtrl.io.phys.col.g
//      io.vgaPhys.col.b(2 downto 0) := 0x7
//      io.vgaPhys.col.b(7 downto 3) := lcvVgaCtrl.io.phys.col.b
//    } otherwise {
//      io.vgaPhys.col := io.vgaPhys.col.getZero
//    }
//
//    val myDoVblankIrq = Bool()
//    myDoVblankIrq := (
//      rose(
//        RegNext(
//          (lcvVgaCtrl.io.misc.vpipeS === LcvVgaState.front),
//          init=False,
//        )
//      )
//    )
//  }
//  //--------
//  val myTempFbInitSoftReset = (
//    rose(myTempSoftReset)
//    //myTempSoftReset
//    //rose(myTempSoftReset)
//    //io.softReset
//    //rose(io.softReset)
//    //|| (
//    //  RegNextWhen(
//    //    False,
//    //    cond=(
//    //      //io.softReset
//    //      myTempSoftReset
//    //    ),
//    //    init=True
//    //  )
//    //)
//  )
//
//  val fbInitArea =
//    new ResetArea(
//      //cartDownload,
//      //rose(io.ioctl.download),
//      //myCartDownloadCond,
//      //myTempRstCondRisingEdge,
//      //rose(io.softReset),
//      //io.softReset,
//      myTempFbInitSoftReset,
//      cumulative=true
//    )
//    //new Area
//  {
//    val vgaTimingInfo = cfg.vgaTimingInfo
//    val fbSize2d = cfg.myFbCfg.fbSize2d
//    val rCnt = (
//      Reg(UInt(log2Up(fbSize2d.y * fbSize2d.x + 1) + 1 bits))
//      init(0x0)
//    )
//    val myTempH2dStm = cloneOf(mySdramCtrlFbInitHost.h2dBus)
//    mySdramCtrlFbInitHost.h2dBus <-/< myTempH2dStm
//    //mySdramCtrlFbInitHost.h2dBus <-< myTempH2dStm
//    //when (
//    //  myTempFbInitSoftReset
//    //) {
//    //  mySdramCtrlFbInitHost.h2dBus.valid := False//True
//    //  mySdramCtrlFbInitHost.h2dBus.payload := (
//    //    mySdramCtrlFbInitHost.h2dBus.payload.getZero
//    //  )
//    //}
//    //mySdramCtrlFbInitHost.h2dBus <-/< myTempH2dStm
//    mySdramCtrlFbInitHost.d2hBus.ready := True
//
//    myTempH2dStm.valid := (
//      (rCnt < ((fbSize2d.y * fbSize2d.x) >> 1))
//      //|| rose(myTempSoftReset)
//      //|| (
//      //  RegNextWhen(
//      //    False,
//      //    cond=(
//      //      //io.ioctl.download
//      //      //cartDownload
//      //      myTempDownloadCond
//      //    ),
//      //    init=True,
//      //  )
//      //)
//    )
//    myTempH2dStm.addr := 0x0
//    myTempH2dStm.addr.allowOverride
//    myTempH2dStm.addr(cfg.fbAddrSliceHi) := True
//    myTempH2dStm.addr(rCnt.high + 2 downto 2) := rCnt
//    myTempH2dStm.byteEn := (
//      U(myTempH2dStm.byteEn.getWidth bits, default -> True)
//    )
//    myTempH2dStm.data := (
//      U(
//        myTempH2dStm.data.getWidth bits,
//        // two blank pixels
//        31 -> False,
//        15 -> False,
//        default -> True//False//True
//      )
//    )
//    myTempH2dStm.src := 0x0
//    myTempH2dStm.isWrite := True
//    when (myTempH2dStm.fire) {
//      rCnt := rCnt + 1
//    }
//    //when (
//    //  //rose(io.softReset)
//    //  rose(myTempSoftReset)
//    //) {
//    //  rCnt := 0x0
//    //} elsewhen (myTempH2dStm.fire) {
//    //  rCnt := rCnt + 1
//    //}
//    myTempH2dStm.burstFirst := (
//      myTempH2dStm.addr(5 downto 2) === 0x0
//    )
//    myTempH2dStm.burstLast := (
//      myTempH2dStm.addr(5 downto 2) === 0xf
//    )
//    myTempH2dStm.burstCnt := 15
//  }
//  //--------
//  val myTempFbAndDcacheSoftReset = (
//    rose(myTempSoftReset)
//    //myTempSoftReset
//  )
//  val fbAndDcacheArea = 
//    //new Area
//    new ResetArea(
//      myTempFbAndDcacheSoftReset,
//      cumulative=true,
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
//
//    mySdramCtrlFbDcacheHost <-/< myFbDcache.io.hiBus
//    //--------
//    //val fbCtrlRstArea = 
//    //  new ResetArea(
//    //    rose(myTempSoftReset),
//    //    cumulative=true,
//    //  )
//    //{
//    //  val myFbCtrl = LcvBusFramebufferCtrl(
//    //    cfg=(
//    //      cfg.myFbCfg
//    //    )
//    //  )
//    //  vgaArea.lcvVgaCtrl.io.push <-/< myFbCtrl.io.pop
//    //}
//    //--------
//    def myFbArbiterHostIdxFbCtrl = 0
//    //def myFbArbiterHostIdxBlockCpu = 1
//    def myFbArbiterHostIdxCpu = 1//2//1
//    def limMyFbArbiterHostIdx = 2//3//2
//
//    //val myFbDebursterSoftReset = (
//    //  LcvBusDevSoftReset(
//    //    cfg=LcvBusDevSoftResetConfig(
//    //      busCfg=(
//    //        //myFbArbTempArea.myFbArbiter.cfg.busCfg
//    //        cfg.myFbCtrlMmapCfg.busCfg
//    //      )
//    //    )
//    //  )
//    //)
//    //myFbDebursterSoftReset.io.softReset := myTempFbAndDcacheSoftReset
//    val myFbDeburster = LcvBusDeburster(
//      cfg=LcvBusDebursterConfig(
//        loBusCfg=(
//          cfg.myFbCtrlMmapCfg.busCfg
//          //myFbDcache.cfg.loBusCfg
//          //myFbArbTempArea.myFbArbiter.cfg.busCfg
//          //cfg.myFbCtrlMmapCfg.busCfg
//        )
//      )
//    )
//
//    val myFbArbTempArea =
//      //new ResetArea(
//      //  myTempFbAndDcacheSoftReset,
//      //  cumulative=true,
//      //)
//      new Area
//    {
//      val myFbArbiter = LcvBusArbiter(
//        cfg=LcvBusArbiterConfig(
//          busCfg=(
//            cfg.myFbCtrlMmapCfg.busCfg
//          ),
//          numHosts=limMyFbArbiterHostIdx,
//          kind=(
//            LcvBusArbiterKind.Priority
//            //LcvBusArbiterKind.RoundRobin
//          )
//        )
//      )
//      myFbArbiter.io.en := (
//        True
//        //!myTempRstCondRisingEdge
//        //!myTempRstCondMain // `cpuArea`'s next reset signal
//      )
//      //myFbArbiter.io.softReset := (
//      //  //rose(io.softReset)
//      //  //rose(myTempSoftReset)
//      //  myTempSoftReset
//      //)
//      myFbArbiter.io.forceHost.valid := False//myTempFbAndDcacheSoftReset//False//myTempSoftReset//False//myTempSoftReset
//      myFbArbiter.io.forceHost.payload := (
//        //0
//        myFbArbiterHostIdxFbCtrl
//      )
//      //val myTempSoftRstArea =
//      //  //new ResetArea(
//      //  //  myTempFbAndDcacheSoftReset,
//      //  //  cumulative=true,
//      //  //)
//      //  new Area
//      //{
//      //  myFbDebursterSoftReset.io.loBus <-/< myFbArbiter.io.dev
//      //}
//      myFbDeburster.io.loBus <-/< myFbArbiter.io.dev
//      //myFbDeburster.io.loBus <-/< myFbDebursterSoftReset.io.hiBus
//      myFbDcache.io.loBus <-/< myFbDeburster.io.hiBus
//    }
//
//    def myFbArbiter = myFbArbTempArea.myFbArbiter
//
//    def myFbArbFbCtrlHost = (
//      myFbArbTempArea.myFbArbiter.io.hostVec(myFbArbiterHostIdxFbCtrl)
//    )
//    //def myFbArbBlockCpuHost = (
//    //  myFbArbiter.io.hostVec(myFbArbiterHostIdxBlockCpu)
//    //)
//    def myFbArbCpuHost = (
//      myFbArbTempArea.myFbArbiter.io.hostVec(myFbArbiterHostIdxCpu)
//    )
//
//    //myFbArbFbCtrlHost <-/< myFbCtrl.io.bus
//
//    //val fbCtrlRstArea = 
//    //  new ResetArea(
//    //    rose(myTempSoftReset),
//    //    cumulative=true,
//    //  )
//    //{
//      val myFbCtrl = LcvBusFramebufferCtrl(
//        cfg=(
//          cfg.myFbCfg
//        )
//      )
//      vgaArea.lcvVgaCtrl.io.push <-/< myFbCtrl.io.pop
//      myFbArbFbCtrlHost <-/< myFbCtrl.io.bus
//    //}
//    //--------
//    //myFbDcache.io.loBus <-/< myFbDeburster.io.hiBus
//    //--------
//  }
//  //--------
//  val myCpuAreaSoftReset = (
//    myTempSoftReset
//    || (
//      RegNextWhen(
//        False,
//        cond=(
//          //io.softReset
//          myTempSoftReset
//        ),
//        init=True
//      )
//    )
//  )
//  mySdramCtrlIcacheHostSoftReset.io.softReset := (
//    myCpuAreaSoftReset
//  )
//  mySdramCtrlNonFbDcacheHostSoftReset.io.softReset := (
//    myCpuAreaSoftReset
//  )
//  val cpuArea =
//    //new Area
//    new ResetArea(
//      myCpuAreaSoftReset,
//      cumulative=true//false//true
//    )
//  {
//    //--------
//    val cpu = SnowHouseCpuWithoutRam(program=cfg.testProgram.program)
//    //--------
//    val irqCtrl = LcvBusIrqCtrl(
//      cfg=LcvBusIrqCtrlConfig(
//        busCfg=cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg.loBusCfg,
//        depth=2,
//      )
//    )
//    cpu.io.idsIraIrq.nextValid := (
//      //!myCpuAreaSoftReset
//      //&& 
//      irqCtrl.io.dstIrq.nextValid
//    )
//    irqCtrl.io.dstIrq.ready := (
//      //!myCpuAreaSoftReset
//      //&& 
//      cpu.io.idsIraIrq.ready
//    )
//
//    def myDoVblankIrq = vgaArea.myDoVblankIrq
//    irqCtrl.io.srcIrqVec(0) := (
//      //!io.softReset
//      //!myCpuAreaSoftReset
//      //&& 
//      myDoVblankIrq
//    )
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
//      irqCtrl.io.srcIrqVec(1) := True//!myCpuAreaSoftReset //True
//    }
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
//        //val tempCond = (
//        //  inp.src
//        //  === (
//        //    RegNextWhen(
//        //      inp.src,
//        //      cond=icache.io.loBus.d2hBus.fire,
//        //    )
//        //    init(0x2)
//        //  )
//        //)
//        //val rState = Reg(Bool(), init=False)
//        //when (
//        //  !myHistCpuIbusD2hFire.last
//        //  || (
//        //    tempCond
//        //    && !rState
//        //  )
//        //) {
//        //  outp.data := outp.data.getZero
//        //  when (inp.src === 0x3) {
//        //    rState := True
//        //  }
//        //}
//      }
//    )
//    cpu.io.lcvIbus.d2hBus <-/< myTempCpuLcvIbusD2hStm
//
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
//    def mySlicedIoNonIrqMmioHost = myDbusSlicer.io.devVec(
//      cfg.myIoNonIrqMmioAddrSliceVal
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
//      io.regFileWriteActive := False
//      io.cpuDbgInfo := cpu.io.dbgInfo
//      when (
//        cpu.io.regFileWriteEnable
//        && !cpu.io.shouldIgnoreInstrAtRegFileWrite
//      ) {
//        io.regFileWriteActive := True
//      }
//    }
//    //--------
//
//    irqCtrl.io.bus <-/< mySlicedIoIrqCtrlHost 
//
//    val nonIrqMmio = MeltedMoonNonIrqMmio(cfg=cfg)
//    nonIrqMmio.io.bus <-/< mySlicedIoNonIrqMmioHost
//    //nonIrqMmio.io.outpChar.simPublic
//    io.outpChar := nonIrqMmio.io.outpChar
//  }
//  //--------
//  val ioctlArea =
//    new Area
//  {
//    io.ioctl.upload_req := False
//    io.ioctl.upload_index := 0x0
//    io.ioctl.din := 0x0
//    //io.ioctl.myWait := False
//    case class MyIoctlPayload(
//      dataWidth: Int,
//    ) extends Bundle {
//      val addr = (
//        //cloneOf(io.ioctl.addr)
//        UInt(cfg.sdramCtrlCfg.busCfg.addrWidth bits)
//      )
//      val data = UInt(dataWidth bits)
//      val isWrite = Bool()
//      val byteEn = UInt(cfg.sdramCtrlCfg.busCfg.byteEnWidth bits)
//    }
//    val myIoctlRecvPushStm = (
//      Vec[Stream[MyIoctlPayload]](
//        List[Stream[MyIoctlPayload]](
//          Stream(MyIoctlPayload(cfg.ioctlSpinalDw)),
//          Stream(MyIoctlPayload(cfg.ioctlSpinalDw)),
//          Stream(MyIoctlPayload(cfg.sdramCtrlCfg.busCfg.dataWidth)),
//          Stream(MyIoctlPayload(cfg.sdramCtrlCfg.busCfg.dataWidth)),
//        )
//      )
//    )
//    val myIoctlPushCntWidth = (
//      log2Up(
//        cfg.sdramCtrlCfg.busCfg.dataWidth
//        / cfg.ioctlSpinalDw
//      ).toInt
//    )
//    myIoctlRecvPushStm(1) <-< myIoctlRecvPushStm.head
//    myIoctlRecvPushStm(1).translateInto(myIoctlRecvPushStm(2))(
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
//        switch (inp.addr(myIoctlPushCntWidth + 1 - 1 downto 1)) {
//          for (idx <- 0 until (1 << myIoctlPushCntWidth)) {
//            is (idx) {
//              //println(
//              //  (idx + 1) * inp.data.getWidth - 1
//              //  downto idx * inp.data.getWidth
//              //)
//              outp.data(
//                (idx + 1) * inp.data.getWidth - 1
//                downto idx * inp.data.getWidth
//              ) := inp.data
//              if (idx == 0) {
//                //when (rIoctlRecvPushLaggingAddrCond) {
//                //  outp.addr := inp.addr - myIoctlLaggingAddr
//                //} otherwise {
//                //  outp.addr := outp.addr.getZero
//                //}
//                //outp.addr := inp.addr - 2
//                outp.addr := inp.addr //- 2
//                outp.isWrite := inp.isWrite//True//False
//                outp.byteEn := (
//                  //U(outp.byteEn.getWidth bits, default -> True)
//                  0x3
//                  //0x0
//                )
//              } else {
//                outp.addr := inp.addr //- 2
//                outp.isWrite := inp.isWrite//True//inp.isWrite
//                outp.byteEn := (
//                  //U(outp.byteEn.getWidth bits, default -> True)
//                  0xc
//                )
//              }
//            }
//          }
//        }
//      }
//    )
//    //myIoctlRecvPushStm.last <-< myIoctlRecvPushStm(2)
//    myIoctlRecvPushStm.last << myIoctlRecvPushStm(2)
//
//    myIoctlRecvPushStm.head.valid := (
//      cartDownload
//    )
//    myIoctlRecvPushStm.head.data := (
//      Mux(
//        cartDownload && io.ioctl.wr,
//        io.ioctl.dout,
//        (
//          RegNext(myIoctlRecvPushStm.head.data)
//          init(0x0)
//        )
//      )
//    )
//    myIoctlRecvPushStm.head.addr := (
//      Mux(
//        cartDownload && io.ioctl.wr,
//        io.ioctl.addr.resize(myIoctlRecvPushStm.head.addr.getWidth),
//        (
//          RegNext(myIoctlRecvPushStm.head.addr)
//          init(0x0)
//        )
//      )
//    )
//    myIoctlRecvPushStm.head.isWrite := (
//      True
//    )
//    io.ioctl.myWait := (
//      //myIoctlAreaResetWire
//      //|| (
//        //ClockDomain.current.readResetWire
//        //|| 
//        cartDownload
//        && !myIoctlRecvPushStm.head.ready
//        //|| rose(myTempSoftReset)
//        //|| rose(io.softReset)
//        //!myIoctlRecvPushStm.head.fire
//      //)
//    )
//    //val myIoctlRecvPopStm = cloneOf(myIoctlRecvFifo.io.pop)
//    val myIoctlRecvPopStm = Stream(
//      MyIoctlPayload(
//        cfg.sdramCtrlCfg.busCfg.dataWidth
//        //cfg.ioctlSpinalDw bits
//      )
//    )
//    //myIoctlRecvPopStm <-< myIoctlRecvPushStm.last//myIoctlRecvFifo.io.pop
//    myIoctlRecvPopStm << myIoctlRecvPushStm.last//myIoctlRecvFifo.io.pop
//    myIoctlRecvPopStm.translateInto(mySdramCtrlIoctlFinalHost.h2dBus)(
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
//    //when (
//    //  //!myIoctlRecvPopStm.valid
//    //) {
//    //  mySdramCtrlIoctlFinalHost.h2dBus.valid := 
//    //}
//    //mySdramCtrlIoctlFinalHost.h2dBus <-/< sdramInitFifo.io.pop
//    mySdramCtrlIoctlFinalHost.d2hBus.ready := True
//  }
//}
//case class MeltedMoonOld2(
//  cfg: MeltedMoonConfig
//) extends Component {
//  //--------
//  val io = MeltedMoonIo(cfg=cfg)
//  noIoPrefix()
//  //--------
//  val cartDownload = (
//    (
//      (
//        //(
//        //  (~io.ioctl.index(5 downto 0).orR)
//        //  && (io.ioctl.index(7 downto 6) === 0x0)
//        //)
//        //||
//        (
//          io.ioctl.index(5 downto 0) === 0x1
//        )
//      )
//      && io.ioctl.download
//    )
//    //|| !io.pllLocked
//  )
//  //def myRstCntWidth = 17//12//25//32//17
//  //val rMyRstCnt = (
//  //  Reg(UInt(myRstCntWidth bits))
//  //  init(1 << (myRstCntWidth - 1) - 1)
//  //)
//  val myTempSoftReset = (
//    //!rMyRstCnt.msb
//    //History[Bool](
//    //  that=(
//    //    io.softReset
//    //  )
//    //)
//    //RegNext(
//    //  io.softReset,
//    //  init=False
//    //)
//
//    History[Bool](
//      that=(
//        //io.softReset
//        //|| 
//        //io.ioctl.download
//        //io.softReset
//        //RegNext(
//        //  //rose(io.softReset),
//        //  io.softReset,
//        //  init=True
//        //)
//        io.softReset
//      ),
//      length=(
//        7
//      ),
//      init=False,
//    ).last//.asBits.asUInt.orR
//
//    //Cat(
//    //  io.softReset,//False//rose(myTempSoftReset)
//    //  RegNext(rose(io.softReset), init=False),
//    //  RegNext(RegNext(rose(io.softReset)), init=False),
//    //  RegNext(RegNext(RegNext(rose(io.softReset))), init=False),
//    //  RegNext(
//    //    RegNext(RegNext(RegNext(rose(io.softReset)))),
//    //    init=False
//    //  ),
//    //).asUInt.orR
//  )
//  val myTempDownloadCond = (
//    io.ioctl.download
//    //cartDownload
//    //myTempSoftReset
//  )
//  //when (
//  //  rose(io.softReset)
//  //  //io.softReset
//  //) {
//  //  rMyRstCnt := 0x0
//  //} elsewhen (myTempSoftReset) {
//  //  rMyRstCnt := rMyRstCnt + 1
//  //}
//
//  //val myTempRstArea =
//  //  new ResetArea(
//  //    io.softReset,
//  //    cumulative=true,
//  //  )
//  //{
//  //  val myTempRstCondMain = (
//  //    RegNext(
//  //      //io.ioctl.download,
//  //      //cartDownload,
//  //      myTempDownloadCond,
//  //      init=False
//  //    )
//  //    || RegNextWhen(
//  //      False,
//  //      cond=(
//  //        //io.ioctl.download
//  //        //cartDownload
//  //        myTempDownloadCond
//  //      ),
//  //      init=True,
//  //    )
//  //  )
//  //  ////val myTempRstCond = (
//  //  ////  myTempRstCondMain
//  //  ////  //RegNext(
//  //  ////  //  //io.ioctl.download,
//  //  ////  //  cartDownload,
//  //  ////  //  init=False
//  //  ////  //)
//  //  ////  || RegNextWhen(
//  //  ////    False,
//  //  ////    cond=(
//  //  ////      //io.ioctl.download
//  //  ////      cartDownload
//  //  ////    ),//cartDownload,
//  //  ////    init=True,
//  //  ////  )
//  //  ////)
//  //  val myTempRstCondRisingEdge = (
//  //    RegNext(
//  //      rose(
//  //        //io.ioctl.download
//  //        //cartDownload
//  //        myTempDownloadCond
//  //      ),
//  //      init=False
//  //    )
//  //  )
//  //}
//  //def myTempRstCondMain = (
//  //  myTempRstArea.myTempRstCondMain
//  //)
//
//  //val myTempRstCondPll = (
//  //  RegNext(
//  //    rose(
//  //      //io.ioctl.download
//  //      cartDownload
//  //    ),
//  //    init=False
//  //  )
//  //  || !io.pllLocked
//  //)
//
//  //val rRstCnt = (
//  //  Reg(SInt(17 bits))
//  //  init(-1)
//  //)
//  //when (
//  //  RegNext(
//  //    rose(
//  //      //io.ioctl.download
//  //      cartDownload
//  //    ),
//  //    init=False
//  //  )
//  //  && rRstCnt.msb
//  //) {
//  //  rRstCnt := S(17 bits, 16 -> False, default -> True)
//  //} elsewhen (!rRstCnt.msb) {
//  //  rRstCnt := rRstCnt - 1 
//  //}
//  //val myCartDownloadCond = (
//  //  !rRstCnt.msb
//  //  //RegNext(
//  //  //  io.ioctl.download,
//  //  //  //cartDownload,
//  //  //  init=False,
//  //  //)
//  //  //|| RegNextWhen(
//  //  //  False,
//  //  //  cond=cartDownload,//io.ioctl.download,//cartDownload,
//  //  //  init=True,
//  //  //)
//  //)
//  //val mySdramCtrl = (
//  //  LcvBusSdramCtrl(
//  //    cfg=cfg.sdramCtrlCfg
//  //  )
//  //)
//  //mySdramCtrl.io.sdram <> io.sdram
//  val myTempSdramCtrlSoftReset = (
//    rose(myTempSoftReset)
//    //myTempSoftReset
//  )
//  val myTestArea =
//    //new Area
//    //new ResetArea(
//    //  myTempSdramCtrlSoftReset,
//    //  cumulative=true//false//true
//    //)
//    //--------
//    // TODO: try `new Area` here...
//    //--------
//    new Area
//  {
//    val mySdramCtrl = (
//      LcvBusSdramCtrl(
//        cfg=cfg.sdramCtrlCfg
//      )
//    )
//    mySdramCtrl.io.sdram <> io.sdram
//    //mySdramCtrl.io.softReset := rose(io.softReset)
//    //val mySdramCtrlSoftReset = (
//    //  LcvBusDevSoftReset(
//    //    cfg=LcvBusDevSoftResetConfig(
//    //      busCfg=cfg.sdramCtrlCfg.busCfg
//    //    )
//    //  )
//    //)
//    //mySdramCtrl.io.bus <-/< mySdramCtrlSoftReset.io.hiBus
//    //mySdramCtrlSoftReset.io.softReset := myTempSdramCtrlSoftReset
//  }
//  def mySdramCtrl = myTestArea.mySdramCtrl
//  //def mySdramCtrlSoftReset = myTestArea.mySdramCtrlSoftReset
//  //mySdramCtrl.io.sdram <> io.sdram
//  //def mySdramCtrlHostIdxFbDcache = 1//0//1//0//1//0//2//1////2 
//  //def mySdramCtrlHostIdxFbInit = 1//2//1
//  //--------
//  def mySdramCtrlHostIdxIoctl = 1//2//0//2//1//0//2//1//0//1//2//0//1//0//2//1//0//1 //
//  //--------
//  def mySdramCtrlHostIdxFbDcache = 0//1//0//1//0//1//0//1//0//1//0//2//1////2 
// // def mySdramCtrlHostIdxFbInit = 2//1//2//1
//  //def mySdramCtrlHostIdxIoctl = 2//1//0//2//1//0//1//2//0//1//0//2//1//0//1 //
//  def mySdramCtrlHostIdxFbInit = 2//1//2//1//2//1//2//1
//  //def mySdramCtrlHostIdxIoctl = 2//1//0//1 //
//  def mySdramCtrlHostIdxIcache = 3//2//3//2//3//2
//  def mySdramCtrlHostIdxNonFbDcache = 4//3//4//3//4//3
//  def limMySdramCtrlHostIdx = 5//4//5//4//5//4
//
//  //val rSdramCtrlArbState = Reg(Bool(), init=False)
//  //val rSdramCtrlArbRstCnt = (
//  //  Reg(UInt(17 bits))
//  //  init(0x0)
//  //)
//  //when (io.softReset) {
//  //}
//
//  val myTempSdramCtrlBusArbiterSoftReset = (
//    rose(myTempSoftReset)
//    //myTempSoftReset
//  )
//  val mySdramCtrlBusArbiterArea =
//    //new ResetArea(
//    //  //RegNext(rose(io.ioctl.download), init=False),
//    //  //myTempRstCondRisingEdge,
//    //  //myTempRstCondRisingEdge || !io.pllLocked,
//    //  //rose(io.softReset),
//    //  //io.softReset,
//    //  myTempSdramCtrlBusArbiterSoftReset,
//    //  cumulative=true,
//    //)
//    new Area
//  {
//    val arbiter =
//    //val mySdramCtrlBusArbiter = 
//    LcvBusArbiter(
//      cfg=LcvBusArbiterConfig(
//        busCfg=cfg.sdramCtrlCfg.busCfg,
//        numHosts=limMySdramCtrlHostIdx, // add 1 for the icache
//        kind=(
//          LcvBusArbiterKind.Priority
//          //LcvBusArbiterKind.RoundRobin
//        ),
//      )
//    )
//    arbiter.io.en := (
//      True
//      //!myTempRstCondRisingEdge
//      //!myTempRstCondMain // `cpuArea`'s next reset signal
//    )
//    //arbiter.io.softReset := (
//    //  //rose(io.softReset)
//    //  //!myTempSoftReset
//    //  rose(myTempSoftReset)
//    //)
//    arbiter.io.forceHost.valid := (
//      //myTempSdramCtrlBusArbiterSoftReset
//      False
//      //Cat(
//      //  rose(myTempSoftReset),//False//rose(myTempSoftReset)
//      //  RegNext(rose(myTempSoftReset), init=False),
//      //  RegNext(RegNext(rose(myTempSoftReset)), init=False),
//      //  RegNext(RegNext(RegNext(rose(myTempSoftReset))), init=False),
//      //  RegNext(
//      //    RegNext(RegNext(RegNext(rose(myTempSoftReset)))),
//      //    init=False
//      //  ),
//      //).asUInt.orR
//    )
//    arbiter.io.forceHost.payload := (
//      //mySdramCtrlHostIdxIoctl
//      //mySdramCtrlHostIdxFbDcache
//      0
//    )
//
//    mySdramCtrl.io.bus <-/< arbiter.io.dev
//    //mySdramCtrlSoftReset.io.loBus <-/< arbiter.io.dev
//  }
//  def mySdramCtrlBusArbiter = (
//    mySdramCtrlBusArbiterArea.arbiter
//  )
//
//  def mySdramCtrlFbDcacheHost = (
//    //sdramArea.mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxFbDcache)
//    mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxFbDcache)
//  )
//  def mySdramCtrlFbInitHost = (
//    //sdramArea.mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxFbInit)
//    mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxFbInit)
//  )
//  def mySdramCtrlIoctlFinalHost = (
//    //sdramArea.mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxIoctl)
//    mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxIoctl)
//  )
//  val mySdramCtrlIcacheHostSoftReset = (
//    LcvBusDevSoftReset(
//      cfg=LcvBusDevSoftResetConfig(
//        busCfg=cfg.sdramCtrlCfg.busCfg
//      )
//    )
//  )
//  mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxIcache) <-/< (
//    mySdramCtrlIcacheHostSoftReset.io.hiBus
//  )
//  def mySdramCtrlIcacheHost = (
//    //sdramArea.mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxIcache)
//    //mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxIcache)
//    mySdramCtrlIcacheHostSoftReset.io.loBus
//  )
//
//  val mySdramCtrlNonFbDcacheHostSoftReset = (
//    LcvBusDevSoftReset(
//      cfg=LcvBusDevSoftResetConfig(
//        busCfg=cfg.sdramCtrlCfg.busCfg
//      )
//    )
//  )
//  mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxNonFbDcache) <-/< (
//    mySdramCtrlNonFbDcacheHostSoftReset.io.hiBus
//  )
//  def mySdramCtrlNonFbDcacheHost = (
//    //sdramArea.mySdramCtrlBusArbiter.io.hostVec(
//    //  mySdramCtrlHostIdxNonFbDcache
//    //)
//    //mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxNonFbDcache)
//    mySdramCtrlNonFbDcacheHostSoftReset.io.loBus
//  )
//  //--------
//  val myTempVgaReset = (
//    //rose(myTempSoftReset)
//    rose(myTempSoftReset)
//    //myTempSoftReset
//  )
//
//  val vgaArea =
//    //new ResetArea(
//    //  //cartDownload,
//    //  //rose(io.ioctl.download),
//    //  //rose(myCartDownloadCond),
//    //  //RegNext(rose(io.ioctl.download), init=False),
//    //  //myTempRstCondRisingEdge,
//    //  //myTempRstCond,
//    //  //myTempRstCondMain,
//    //  //myTempRstCondRisingEdge,
//    //  //io.softReset,
//    //  //rose(io.softReset),
//    //  //io.softReset,
//    //  //rose(myTempSoftReset),
//    //  myTempVgaReset,
//    //  cumulative=true
//    //)
//    new Area
//  {
//    val vgaTimingInfo = cfg.vgaTimingInfo
//    val lcvVgaCtrl = (
//      LcvVgaCtrl(
//        clkRate=cfg.clkRate,
//        rgbConfig=cfg.rgbCfg,
//        vgaTimingInfo=vgaTimingInfo,
//        fifoDepth=(
//          1
//        ),
//      )
//    )
//    lcvVgaCtrl.io.fifoFlush := False
//    io.vgaPhys.setAsReg() init(io.vgaPhys.getZero)
//    io.vgaVisib.setAsReg() init(io.vgaVisib.getZero)
//    io.vgaPixelEn.setAsReg() init(io.vgaPixelEn.getZero)
//
//    io.vgaPhys.hsync := lcvVgaCtrl.io.phys.hsync
//    io.vgaPhys.vsync := lcvVgaCtrl.io.phys.vsync
//    io.vgaVisib := lcvVgaCtrl.io.misc.visib
//    io.vgaPixelEn := lcvVgaCtrl.io.misc.pixelEn
//
//    lcvVgaCtrl.io.en := True
//    when (lcvVgaCtrl.io.misc.visib) {
//      io.vgaPhys.col.r(2 downto 0) := 0x7
//      io.vgaPhys.col.r(7 downto 3) := lcvVgaCtrl.io.phys.col.r
//      io.vgaPhys.col.g(2 downto 0) := 0x7
//      io.vgaPhys.col.g(7 downto 3) := lcvVgaCtrl.io.phys.col.g
//      io.vgaPhys.col.b(2 downto 0) := 0x7
//      io.vgaPhys.col.b(7 downto 3) := lcvVgaCtrl.io.phys.col.b
//    } otherwise {
//      io.vgaPhys.col := io.vgaPhys.col.getZero
//    }
//
//    val myDoVblankIrq = Bool()
//    myDoVblankIrq := (
//      rose(
//        RegNext(
//          (lcvVgaCtrl.io.misc.vpipeS === LcvVgaState.front),
//          init=False,
//        )
//      )
//    )
//  }
//
//  val myTempFbInitSoftReset = (
//    //rose(myTempSoftReset)
//    myTempSoftReset
//    //rose(myTempSoftReset)
//    //io.softReset
//    //rose(io.softReset)
//    || (
//      RegNextWhen(
//        False,
//        cond=(
//          //io.softReset
//          myTempSoftReset
//        ),
//        init=True
//      )
//    )
//  )
//
//  val fbInitArea =
//    new ResetArea(
//      //cartDownload,
//      //rose(io.ioctl.download),
//      //myCartDownloadCond,
//      //myTempRstCondRisingEdge,
//      //rose(io.softReset),
//      //io.softReset,
//      myTempFbInitSoftReset,
//      cumulative=true
//    )
//    new Area
//  {
//    val vgaTimingInfo = cfg.vgaTimingInfo
//    val fbSize2d = cfg.myFbCfg.fbSize2d
//    val rCnt = (
//      Reg(UInt(log2Up(fbSize2d.y * fbSize2d.x + 1) + 1 bits))
//      init(0x0)
//    )
//    val myTempH2dStm = cloneOf(mySdramCtrlFbInitHost.h2dBus)
//    mySdramCtrlFbInitHost.h2dBus <-/< myTempH2dStm
//    //mySdramCtrlFbInitHost.h2dBus <-< myTempH2dStm
//    when (
//      myTempFbInitSoftReset
//    ) {
//      mySdramCtrlFbInitHost.h2dBus.valid := False//True
//      mySdramCtrlFbInitHost.h2dBus.payload := (
//        mySdramCtrlFbInitHost.h2dBus.payload.getZero
//      )
//    }
//    //mySdramCtrlFbInitHost.h2dBus <-/< myTempH2dStm
//    mySdramCtrlFbInitHost.d2hBus.ready := True
//
//    myTempH2dStm.valid := (
//      (rCnt < ((fbSize2d.y * fbSize2d.x) >> 1))
//      //|| rose(myTempSoftReset)
//      //|| (
//      //  RegNextWhen(
//      //    False,
//      //    cond=(
//      //      //io.ioctl.download
//      //      //cartDownload
//      //      myTempDownloadCond
//      //    ),
//      //    init=True,
//      //  )
//      //)
//    )
//    myTempH2dStm.addr := 0x0
//    myTempH2dStm.addr.allowOverride
//    myTempH2dStm.addr(cfg.fbAddrSliceHi) := True
//    myTempH2dStm.addr(rCnt.high + 2 downto 2) := rCnt
//    myTempH2dStm.byteEn := (
//      U(myTempH2dStm.byteEn.getWidth bits, default -> True)
//    )
//    myTempH2dStm.data := (
//      U(
//        myTempH2dStm.data.getWidth bits,
//        // two blank pixels
//        31 -> False,
//        15 -> False,
//        default -> True//False//True
//      )
//    )
//    myTempH2dStm.src := 0x0
//    myTempH2dStm.isWrite := True
//    when (myTempH2dStm.fire) {
//      rCnt := rCnt + 1
//    }
//    //when (
//    //  //rose(io.softReset)
//    //  rose(myTempSoftReset)
//    //) {
//    //  rCnt := 0x0
//    //} elsewhen (myTempH2dStm.fire) {
//    //  rCnt := rCnt + 1
//    //}
//    myTempH2dStm.burstFirst := (
//      myTempH2dStm.addr(5 downto 2) === 0x0
//    )
//    myTempH2dStm.burstLast := (
//      myTempH2dStm.addr(5 downto 2) === 0xf
//    )
//    myTempH2dStm.burstCnt := 15
//  }
//
//  //io.ioctl.myWait := True
//  //--------
//  val myTempFbAndDcacheSoftReset = (
//    rose(myTempSoftReset)
//    //myTempSoftReset
//  )
//  val fbAndDcacheArea = 
//    new Area
//    //new ResetArea(
//    //  //myInnerResetCond,
//    //  //myMainResetCond,
//    //  //cartDownload,
//    //  //io.ioctl.download,
//    //  //rose(io.ioctl.download),
//    //  //rose(io.ioctl.download),
//    //  //rose(myCartDownloadCond),
//    //  //RegNext(rose(io.ioctl.download), init=False),
//    //  //myTempRstCondRisingEdge,
//    //  //myTempRstCond,
//    //  //myTempRstCondMain,
//    //  //rose(io.softReset),
//    //  //io.softReset,
//    //  //rose(myTempSoftReset),
//    //  //myTempSoftReset,
//    //  myTempFbAndDcacheSoftReset,
//    //  cumulative=true//false//true//false//true
//    //)
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
//    //val fbCtrlRstArea = 
//    //  new ResetArea(
//    //    rose(myTempSoftReset),
//    //    cumulative=true,
//    //  )
//    //{
//    //  val myFbCtrl = LcvBusFramebufferCtrl(
//    //    cfg=(
//    //      cfg.myFbCfg
//    //    )
//    //  )
//    //  vgaArea.lcvVgaCtrl.io.push <-/< myFbCtrl.io.pop
//    //}
//    //--------
//    def myFbArbiterHostIdxFbCtrl = 0
//    //def myFbArbiterHostIdxBlockCpu = 1
//    def myFbArbiterHostIdxCpu = 1//2//1
//    def limMyFbArbiterHostIdx = 2//3//2
//
//    //val myFbDebursterSoftReset = (
//    //  LcvBusDevSoftReset(
//    //    cfg=LcvBusDevSoftResetConfig(
//    //      busCfg=(
//    //        //myFbArbTempArea.myFbArbiter.cfg.busCfg
//    //        cfg.myFbCtrlMmapCfg.busCfg
//    //      )
//    //    )
//    //  )
//    //)
//    //myFbDebursterSoftReset.io.softReset := myTempFbAndDcacheSoftReset
//    val myFbDeburster = LcvBusDeburster(
//      cfg=LcvBusDebursterConfig(
//        loBusCfg=(
//          cfg.myFbCtrlMmapCfg.busCfg
//          //myFbDcache.cfg.loBusCfg
//          //myFbArbTempArea.myFbArbiter.cfg.busCfg
//          //cfg.myFbCtrlMmapCfg.busCfg
//        )
//      )
//    )
//
//    val myFbArbTempArea =
//      //new ResetArea(
//      //  myTempFbAndDcacheSoftReset,
//      //  cumulative=true,
//      //)
//      new Area
//    {
//      val myFbArbiter = LcvBusArbiter(
//        cfg=LcvBusArbiterConfig(
//          busCfg=(
//            cfg.myFbCtrlMmapCfg.busCfg
//          ),
//          numHosts=limMyFbArbiterHostIdx,
//          kind=(
//            LcvBusArbiterKind.Priority
//            //LcvBusArbiterKind.RoundRobin
//          )
//        )
//      )
//      myFbArbiter.io.en := (
//        True
//        //!myTempRstCondRisingEdge
//        //!myTempRstCondMain // `cpuArea`'s next reset signal
//      )
//      //myFbArbiter.io.softReset := (
//      //  //rose(io.softReset)
//      //  //rose(myTempSoftReset)
//      //  myTempSoftReset
//      //)
//      myFbArbiter.io.forceHost.valid := False//myTempFbAndDcacheSoftReset//False//myTempSoftReset//False//myTempSoftReset
//      myFbArbiter.io.forceHost.payload := (
//        //0
//        myFbArbiterHostIdxFbCtrl
//      )
//      //val myTempSoftRstArea =
//      //  //new ResetArea(
//      //  //  myTempFbAndDcacheSoftReset,
//      //  //  cumulative=true,
//      //  //)
//      //  new Area
//      //{
//      //  myFbDebursterSoftReset.io.loBus <-/< myFbArbiter.io.dev
//      //}
//      myFbDeburster.io.loBus <-/< myFbArbiter.io.dev
//      //myFbDeburster.io.loBus <-/< myFbDebursterSoftReset.io.hiBus
//      myFbDcache.io.loBus <-/< myFbDeburster.io.hiBus
//    }
//
//    def myFbArbiter = myFbArbTempArea.myFbArbiter
//
//    def myFbArbFbCtrlHost = (
//      myFbArbTempArea.myFbArbiter.io.hostVec(myFbArbiterHostIdxFbCtrl)
//    )
//    //def myFbArbBlockCpuHost = (
//    //  myFbArbiter.io.hostVec(myFbArbiterHostIdxBlockCpu)
//    //)
//    def myFbArbCpuHost = (
//      myFbArbTempArea.myFbArbiter.io.hostVec(myFbArbiterHostIdxCpu)
//    )
//
//    //myFbArbFbCtrlHost <-/< myFbCtrl.io.bus
//
//    //val fbCtrlRstArea = 
//    //  new ResetArea(
//    //    rose(myTempSoftReset),
//    //    cumulative=true,
//    //  )
//    //{
//      val myFbCtrl = LcvBusFramebufferCtrl(
//        cfg=(
//          cfg.myFbCfg
//        )
//      )
//      vgaArea.lcvVgaCtrl.io.push <-/< myFbCtrl.io.pop
//      myFbArbFbCtrlHost <-/< myFbCtrl.io.bus
//    //}
//    //--------
//    //myFbDcache.io.loBus <-/< myFbDeburster.io.hiBus
//    //--------
//  }
//  //--------
//  val myCpuAreaSoftReset = (
//    //myTempRstCondMain || 
//    myTempSoftReset
//    //rose(myTempSoftReset)
//    //io.softReset
//    //rose(io.softReset)
//    || (
//      RegNextWhen(
//        False,
//        cond=(
//          //io.softReset
//          myTempSoftReset
//        ),
//        init=True
//      )
//    )
//  )
//  mySdramCtrlIcacheHostSoftReset.io.softReset := (
//    myCpuAreaSoftReset
//  )
//  mySdramCtrlNonFbDcacheHostSoftReset.io.softReset := (
//    myCpuAreaSoftReset
//  )
//  val cpuArea =
//    //new Area
//    new ResetArea(
//      //myTempRstCond,
//      //myTempRstCondMain,
//      //myCpuAreaSoftReset,
//      //rose(io.softReset),
//      //io.softReset,
//      myCpuAreaSoftReset,
//      cumulative=true//false//true
//    )
//  {
//    //--------
//    val cpu = SnowHouseCpuWithoutRam(program=cfg.testProgram.program)
//    //def cpu = 
//
//    //if (cfg.dbgUseLcvBusMem) {
//    //  for (idx <- 0 until myCpuDbusWriteSearchArr.size) {
//    //    val myAddr = myCpuDbusWriteSearchArr(idx).addr
//    //    val myData = myCpuDbusWriteSearchArr(idx).data
//
//    //    myCpuDbusWriteSearchCmpEqVec(idx) := (
//    //      cpu.io.lcvDbus.h2dBus.fire
//    //      && cpu.io.lcvDbus.h2dBus.isWrite
//    //      && cpu.io.lcvDbus.h2dBus.addr === myAddr
//    //      && (myData match {
//    //        case Some(data) => (
//    //          cpu.io.lcvDbus.h2dBus.data === data
//    //        )
//    //        case None => (
//    //          True
//    //        )
//    //      })
//    //    )
//    //    //when ((myCpuDbusWriteSearchCmpEqVec(idx))) {
//    //    //  report(Seq(
//    //    //    s"myCpuDbusWriteSearchCmpEqVec(${idx}): ",
//    //    //    cpu.io.lcvDbus.h2dBus.addr,
//    //    //    " ",
//    //    //    cpu.io.lcvDbus.h2dBus.data,
//    //    //  ))
//    //    //}
//    //  }
//    //}
//    //--------
//    val irqCtrl = LcvBusIrqCtrl(
//      cfg=LcvBusIrqCtrlConfig(
//        busCfg=cfg.cpuCfg.shCfg.subCfg.lcvDbusEtcCfg.loBusCfg,
//        depth=2,
//      )
//    )
//    cpu.io.idsIraIrq.nextValid := (
//      //!io.softReset
//      //!myCpuAreaSoftReset
//      //&& 
//      irqCtrl.io.dstIrq.nextValid
//    )
//    irqCtrl.io.dstIrq.ready := (
//      //!io.softReset
//      //!myCpuAreaSoftReset
//      //&& 
//      cpu.io.idsIraIrq.ready
//    )
//
//    def myDoVblankIrq = vgaArea.myDoVblankIrq
//    irqCtrl.io.srcIrqVec(0) := (
//      //!io.softReset
//      //!myCpuAreaSoftReset
//      //&& 
//      myDoVblankIrq
//    )
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
//      irqCtrl.io.srcIrqVec(1) := True//!myCpuAreaSoftReset //True
//    }
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
//        //val tempCond = (
//        //  inp.src
//        //  === (
//        //    RegNextWhen(
//        //      inp.src,
//        //      cond=icache.io.loBus.d2hBus.fire,
//        //    )
//        //    init(0x2)
//        //  )
//        //)
//        //val rState = Reg(Bool(), init=False)
//        //when (
//        //  !myHistCpuIbusD2hFire.last
//        //  || (
//        //    tempCond
//        //    && !rState
//        //  )
//        //) {
//        //  outp.data := outp.data.getZero
//        //  when (inp.src === 0x3) {
//        //    rState := True
//        //  }
//        //}
//      }
//    )
//    cpu.io.lcvIbus.d2hBus <-/< myTempCpuLcvIbusD2hStm
//
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
//    def mySlicedIoNonIrqMmioHost = myDbusSlicer.io.devVec(
//      cfg.myIoNonIrqMmioAddrSliceVal
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
//      io.regFileWriteActive := False
//      io.cpuDbgInfo := cpu.io.dbgInfo
//      when (
//        cpu.io.regFileWriteEnable
//        && !cpu.io.shouldIgnoreInstrAtRegFileWrite
//      ) {
//        io.regFileWriteActive := True
//      }
//    }
//    //--------
//
//    irqCtrl.io.bus <-/< mySlicedIoIrqCtrlHost 
//
//    val nonIrqMmio = MeltedMoonNonIrqMmio(cfg=cfg)
//    nonIrqMmio.io.bus <-/< mySlicedIoNonIrqMmioHost
//    //nonIrqMmio.io.outpChar.simPublic
//    io.outpChar := nonIrqMmio.io.outpChar
//  }
//  //when (
//  //  myCpuAreaSoftReset
//  //  //|| RegNextWhen(
//  //  //  False,
//  //  //  cond=myCpuAreaSoftReset,
//  //  //  init=True,
//  //  //)
//  //) {
//  //  //myFbArbBlockCpuHost.h2dBus.valid := True
//  //  mySdramCtrlIcacheHost.h2dBus.valid := False
//  //  mySdramCtrlIcacheHost.d2hBus.ready := True
//  //  mySdramCtrlNonFbDcacheHost.h2dBus.valid := False
//  //  mySdramCtrlNonFbDcacheHost.d2hBus.ready := True
//  //  fbAndDcacheArea.myFbArbCpuHost.h2dBus.valid := False
//  //  fbAndDcacheArea.myFbArbCpuHost.d2hBus.ready := True
//  //}
//
//  //val myIoctlAreaResetWire = (
//  //  //myTempRstCondRisingEdge
//  //  //rose(io.softReset)
//  //  //rose(myTempSoftReset)
//  //  myTempSoftReset
//  //)
//
//  val ioctlArea =
//    //new ResetArea(
//    //  myIoctlAreaResetWire,
//    //  //rose(io.softReset),
//    //  //io.softReset,
//    //  cumulative=true,//false//true,
//    //)
//    new Area
//  {
//    io.ioctl.upload_req := False
//    io.ioctl.upload_index := 0x0
//    io.ioctl.din := 0x0
//    //io.ioctl.myWait := False
//    case class MyIoctlPayload(
//      dataWidth: Int,
//    ) extends Bundle {
//      val addr = (
//        //cloneOf(io.ioctl.addr)
//        UInt(cfg.sdramCtrlCfg.busCfg.addrWidth bits)
//      )
//      val data = UInt(dataWidth bits)
//      val isWrite = Bool()
//      val byteEn = UInt(cfg.sdramCtrlCfg.busCfg.byteEnWidth bits)
//    }
//    val myIoctlRecvPushStm = (
//      Vec[Stream[MyIoctlPayload]](
//        List[Stream[MyIoctlPayload]](
//          Stream(MyIoctlPayload(cfg.ioctlSpinalDw)),
//          Stream(MyIoctlPayload(cfg.ioctlSpinalDw)),
//          Stream(MyIoctlPayload(cfg.sdramCtrlCfg.busCfg.dataWidth)),
//          Stream(MyIoctlPayload(cfg.sdramCtrlCfg.busCfg.dataWidth)),
//        )
//      )
//    )
//    val myIoctlPushCntWidth = (
//      log2Up(
//        cfg.sdramCtrlCfg.busCfg.dataWidth
//        / cfg.ioctlSpinalDw
//      ).toInt
//    )
//    myIoctlRecvPushStm(1) <-< myIoctlRecvPushStm.head
//    myIoctlRecvPushStm(1).translateInto(myIoctlRecvPushStm(2))(
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
//        switch (inp.addr(myIoctlPushCntWidth + 1 - 1 downto 1)) {
//          for (idx <- 0 until (1 << myIoctlPushCntWidth)) {
//            is (idx) {
//              //println(
//              //  (idx + 1) * inp.data.getWidth - 1
//              //  downto idx * inp.data.getWidth
//              //)
//              outp.data(
//                (idx + 1) * inp.data.getWidth - 1
//                downto idx * inp.data.getWidth
//              ) := inp.data
//              if (idx == 0) {
//                //when (rIoctlRecvPushLaggingAddrCond) {
//                //  outp.addr := inp.addr - myIoctlLaggingAddr
//                //} otherwise {
//                //  outp.addr := outp.addr.getZero
//                //}
//                //outp.addr := inp.addr - 2
//                outp.addr := inp.addr //- 2
//                outp.isWrite := inp.isWrite//True//False
//                outp.byteEn := (
//                  //U(outp.byteEn.getWidth bits, default -> True)
//                  0x3
//                  //0x0
//                )
//              } else {
//                outp.addr := inp.addr //- 2
//                outp.isWrite := inp.isWrite//True//inp.isWrite
//                outp.byteEn := (
//                  //U(outp.byteEn.getWidth bits, default -> True)
//                  0xc
//                )
//              }
//            }
//          }
//        }
//      }
//    )
//    //myIoctlRecvPushStm.last <-/< myIoctlRecvPushStm(1).throwWhen(
//    //  !myIoctlRecvPushStm.last.isWrite
//    //)
//    //myIoctlRecvPushStm.last <-< myIoctlRecvPushStm(2)
//    myIoctlRecvPushStm.last << myIoctlRecvPushStm(2)
//
//    myIoctlRecvPushStm.head.valid := (
//      //RegNext(
//        //cartDownload && io.ioctl.wr,
//        //io.ioctl.download && io.ioctl.wr//,
//        //cartDownload && io.ioctl.wr//,
//        //cartDownload
//        myTempDownloadCond
//        //|| RegNextWhen(
//        //  False,
//        //  cond=(
//        //    //cartDownload
//        //    myTempDownloadCond
//        //  ),
//        //  init=True,
//        //)
//        //myCartDownloadCond
//      //  init=False
//      //)
//      //&& io.ioctl.wr
//    )
//    myIoctlRecvPushStm.head.data := (
//      //RegNext(
//        Mux(
//          myTempDownloadCond && io.ioctl.wr,
//          io.ioctl.dout,
//          (
//            RegNext(myIoctlRecvPushStm.head.data)
//            init(0x0)
//          )
//        )
//      //)
//      //init(0x0)
//    )
//    myIoctlRecvPushStm.head.addr := (
//      //RegNext(
//        //io.ioctl.addr.resize(myIoctlRecvPushStm.head.addr.getWidth),
//        Mux(
//          //cartDownload,
//          myTempDownloadCond && io.ioctl.wr,
//          io.ioctl.addr.resize(myIoctlRecvPushStm.head.addr.getWidth),
//          (
//            RegNext(myIoctlRecvPushStm.head.addr)
//            init(0x0)
//          )
//          //myIoctlRecvPushStm.head.addr.getZero,
//        )
//      //)
//      //init(0x0)
//    )
//    myIoctlRecvPushStm.head.isWrite := (
//      //io.ioctl.wr
//      //RegNext(
//      //  cartDownload && io.ioctl.wr,
//      //  init=False
//      //)
//      True
//      //io.ioctl.wr
//      //True
//    )
//    io.ioctl.myWait := (
//      //cartDownload
//      //&& io.ioctl.wr
//      //&& 
//      //fbInitArea.myTempH2dStm.valid
//      //|| 
//      //RegNext(cartDownload, init=False)
//      //RegNext(io.ioctl.download, init=False)
//      //&& 
//
//      //myIoctlRecvPushStm.head.valid
//
//      //io.ioctl.download
//      //&&
//
//      //cartDownload
//      //myIoctlRecvPushStm.head.valid
//      //cartDownload
//
//      //myTempDownloadCond
//      //&& 
//      //myIoctlAreaResetWire
//      //|| (
//        //ClockDomain.current.readResetWire
//        //|| 
//        !myIoctlRecvPushStm.head.ready
//        //|| rose(myTempSoftReset)
//        //|| rose(io.softReset)
//        //!myIoctlRecvPushStm.head.fire
//      //)
//    )
//    //val myIoctlRecvPopStm = cloneOf(myIoctlRecvFifo.io.pop)
//    val myIoctlRecvPopStm = Stream(
//      MyIoctlPayload(
//        cfg.sdramCtrlCfg.busCfg.dataWidth
//        //cfg.ioctlSpinalDw bits
//      )
//    )
//    //myIoctlRecvPopStm <-< myIoctlRecvPushStm.last//myIoctlRecvFifo.io.pop
//    myIoctlRecvPopStm << myIoctlRecvPushStm.last//myIoctlRecvFifo.io.pop
//    myIoctlRecvPopStm.translateInto(mySdramCtrlIoctlFinalHost.h2dBus)(
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
//    //when (
//    //  //!myIoctlRecvPopStm.valid
//    //) {
//    //  mySdramCtrlIoctlFinalHost.h2dBus.valid := 
//    //}
//    //mySdramCtrlIoctlFinalHost.h2dBus <-/< sdramInitFifo.io.pop
//    mySdramCtrlIoctlFinalHost.d2hBus.ready := True
//  }
//  //when (
//  //  !ClockDomain.current.readResetWire
//  //  && myCartDownloadCond
//  //) {
//  //  fbAndDcacheArea.myFbArbCpuHost.d2hBus.ready := True
//  //  mySdramCtrlIcacheHost.d2hBus.ready := True
//  //  mySdramCtrlNonFbDcacheHost.d2hBus.ready := True
//  //}
//  //--------
//  //when (
//  //  //myCartDownloadCond
//  //  //myTempRstCondRisingEdge
//  //  myIoctlAreaResetWire
//  //  //|| RegNextWhen(
//  //  //  False,
//  //  //  cond=myIoctlAreaResetWire,
//  //  //  init=True,
//  //  //)
//  //) {
//  //  //mySdramCtrlIoctlFinalHost.h2dBus.valid := False
//  //  //mySdramCtrlIoctlFinalHost.d2hBus.ready := False
//
//  //  io.ioctl.upload_req := False
//  //  io.ioctl.upload_index := 0x0
//  //  io.ioctl.din := 0x0
//  //  io.ioctl.myWait := True
//  //  //mySdramCtrl.io.bus.d2hBus.ready := True
//  //  //mySdramCtrl.io.bus.h2dBus.valid := False
//  //}
//  //when (rose(myCartDownloadCond)) {
//  //  io.ioctl.myWait := True
//  //}
//
//}
//case class MeltedMoon(
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
//    //        //io.ioctl.download
//    //        cartDownload
//    //      ),
//    //      init=True,
//    //    )
//    //    //!rCartDownloadState
//    //    //|| 
//    //    || myInnerResetCond
//
//    //    //|| sdramInitFifo.io.pop.valid
//    //    //|| io.ioctl.download
//    //    //|| sdramInitFifo.io.pop.valid
//    ////  ),
//    ////  init=True
//    ////)
//    RegNext(
//      (
//        //cartDownload
//        io.ioctl.download
//        //rose(io.ioctl.download)
//      ),
//      init=False
//    )
//    //|| rose(io.ioctl.download)
//    //|| ClockDomain.current.readResetWire
//    || (
//      RegNextWhen(
//        False,
//        cond=(
//          //cartDownload
//          io.ioctl.download
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
//    //  //io.ioctl.download,
//    //  cumulative=true//true//false//true
//    //)
//    //new ResetArea(
//    //  //rose(myInnerResetCond),
//    //  rose(io.ioctl.download),
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
//    val arbiter = LcvBusArbiter(
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
//    arbiter.io.en := True
//    arbiter.io.forceHost := arbiter.io.forceHost.getZero
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
//      mySdramCtrl.io.bus <-/< arbiter.io.dev
//    } else {
//      myDbgLcvBusMem.io.bus <-/< arbiter.io.dev
//    }
//
//    def mySdramCtrlFbDcacheHost = (
//      arbiter.io.hostVec(mySdramCtrlHostIdxFbDcache)
//    )
//    //def mySdramCtrlFbInitHost = (
//    //  mySdramCtrlBusArbiter.io.hostVec(mySdramCtrlHostIdxFbInit)
//    //)
//    def mySdramCtrlIoctlFinalHost = (
//      arbiter.io.hostVec(mySdramCtrlHostIdxIoctl)
//    )
//    def mySdramCtrlIcacheHost = (
//      arbiter.io.hostVec(mySdramCtrlHostIdxIcache)
//    )
//    def mySdramCtrlNonFbDcacheHost = (
//      arbiter.io.hostVec(mySdramCtrlHostIdxNonFbDcache)
//    )
//    //val sdramInitFifo = (
//    //  !cfg.dbgUseLcvBusMem
//    //) generate (
//    //  StreamFifo(
//    //    dataType=cloneOf(mySdramCtrlIoctlFinalHost.h2dBus.payload),
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
//      mySdramCtrlIoctlFinalHost.h2dBus.valid := False
//      mySdramCtrlIoctlFinalHost.h2dBus.payload := (
//        mySdramCtrlIoctlFinalHost.h2dBus.payload.getZero
//      )
//      mySdramCtrlIoctlFinalHost.d2hBus.ready := False
//    }
//  }
//  def mySdramCtrlFbDcacheHost = (
//    mySdramCtrlArea.mySdramCtrlFbDcacheHost
//  )
//  //def mySdramCtrlFbInitHost = (
//  //  mySdramCtrlArea.mySdramCtrlFbInitHost
//  //)
//  def mySdramCtrlIoctlFinalHost = (
//    mySdramCtrlArea.mySdramCtrlIoctlFinalHost
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
//          (~io.ioctl.index(5 downto 0).orR)
//          && (io.ioctl.index(7 downto 6) === 0x0)
//        )
//        || (
//          io.ioctl.index(5 downto 0) === 0x1
//        )
//      )
//      && io.ioctl.download
//      //io.ioctl.wr
//      //&& io.ioctl.download
//      //&& io.ioctl.index(5 downto 0) === 0x1
//      ////&& codeIndex
//      ////&& !codeIndex
//      ////&& (io.ioctl.index =/= 4)
//      ////&& (io.ioctl.index =/= 254)
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
//  //    io.ioctl.download
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
//  //  io.ioctl.download
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
//      //io.ioctl.download,
//      rose(io.ioctl.download),
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
//      rose(io.ioctl.download),
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
//    //  rose(io.ioctl.download),
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
//        dataType=cloneOf(mySdramCtrlIoctlFinalHost.h2dBus.payload),
//        depth=(
//          32,
//        ),
//        latency=2,
//        forFMax=true,
//        //pushClock=ioctlClkDomain,
//        //popClock=ClockDomain.current,
//      )
//    )
//    io.ioctl.upload_req := False
//    io.ioctl.upload_index := 0x0
//    io.ioctl.din := 0x0
//    //io.ioctl.wait := False
//
//    case class MyIoctlPayload(
//      dataWidth: Int,
//    ) extends Bundle {
//      val addr = (
//        //cloneOf(io.ioctl.addr)
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
//    //val codeIndex = io.ioctl.index.orR//andR
//    //val codeDownload = io.ioctl.download && codeIndex
//    //val myIoctlRecvPushValidCond = (
//    //  //io.ioctl.wr && io.ioctl.download && cartDownload
//    //  cartDownload
//    //)
//    //myPixelMuxSel.lsb := vgaArea.stickySeenMyPixelPushStmValid
//    myIoctlRecvPushStm.head.valid := cartDownload
//    myIoctlRecvPushStm.head.data := (
//      io.ioctl.dout
//      //Mux(
//      //  cartDownload && io.ioctl.wr,
//      //  io.ioctl.dout,
//      //  RegNext(
//      //    myIoctlRecvPushStm.head.data,
//      //    init=myIoctlRecvPushStm.head.data.getZero,
//      //  )
//      //)
//    )
//    myIoctlRecvPushStm.head.addr := (
//      //Mux(
//      //  cartDownload && io.ioctl.wr,
//      //  io.ioctl.addr.resize(myIoctlRecvPushStm.head.addr.getWidth),
//      //  RegNext(
//      //    myIoctlRecvPushStm.head.addr,
//      //    init=myIoctlRecvPushStm.head.addr.getZero,
//      //  )
//      //)
//      io.ioctl.addr.resize(myIoctlRecvPushStm.head.addr.getWidth)
//    )
//    myIoctlRecvPushStm.head.isWrite := (
//      io.ioctl.wr
//      //True
//      //cartDownload
//      //&& io.ioctl.wr
//      //|| RegNextWhen(
//      //  True,
//      //  cond=cartDownload,
//      //  init=False,
//      //)
//    )
//    io.ioctl.myWait := (
//      cartDownload
//      //&& io.ioctl.wr
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
//    mySdramCtrlIoctlFinalHost.h2dBus <-< sdramInitFifo.io.pop
//    mySdramCtrlIoctlFinalHost.d2hBus.ready := True
//  })
//  //--------
//  val fbAndDcacheArea = 
//    new Area 
//    //new ResetArea(
//    //  //myInnerResetCond,
//    //  //myMainResetCond,
//    //  //cartDownload,
//    //  //io.ioctl.download,
//    //  rose(io.ioctl.download),
//    //  cumulative=true//false//true//false//true
//    //)
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
//    myFbArbiter.io.forceHost := myFbArbiter.io.forceHost.getZero
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
//    def mySlicedIoNonIrqMmioHost = myDbusSlicer.io.devVec(
//      cfg.myIoNonIrqMmioAddrSliceVal
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
//    val nonIrqMmio = MeltedMoonNonIrqMmio(cfg=cfg)
//    nonIrqMmio.io.bus <-/< mySlicedIoNonIrqMmioHost
//    //nonIrqMmio.io.outpChar.simPublic
//    io.outpChar := nonIrqMmio.io.outpChar
//
//    //mySlicedIoNonIrqMmioHost.h2dBus.ready := False
//    //mySlicedIoNonIrqMmioHost.d2hBus.valid := False
//    //mySlicedIoNonIrqMmioHost.d2hBus.payload := (
//    //  // this locks up the CPU! eek!
//    //  mySlicedIoNonIrqMmioHost.d2hBus.payload.getZero
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
  val meltedMoon = (
    MeltedMoon(cfg=cfg)
    //MeltedMoonFbDdram(cfg=cfg)
  )
  val myDbgDdramArea = (
    meltedMoon.io.ddram != null
  ) generate (new Area {
    //val myHistSetDdramBusy = History[Bool](
    //  that=False,
    //  length=32,
    //  init=True,
    //)
    //meltedMoon.io.ddram.busy := myHistSetDdramBusy.last

    val myPrngCfg = LcvXorShift16Config(
      xsInitS2d={
        val tempA2d = new ArrayBuffer[Seq[BigInt]]()
        val outerSize = (
          //cfg.busCfg.maxBurstSizeMinus1 + 1
          //+ (if (cfg.kind._hasRandAddr) (1) else (0))
          //16
          //4
          //1
          2
        )
        val innerSize = 2//1//2
        for (idx <- 0 until outerSize) {
          val tempArr = new ArrayBuffer[BigInt]()
          for (jdx <- 0 until innerSize) {
            tempArr += (
              BigInt(idx) * BigInt(innerSize) + BigInt(jdx) + 1
            )
          }
          tempA2d += tempArr
        }
        tempA2d
      }
    )
    val myPrng = LcvXorShift16(cfg=myPrngCfg)
    meltedMoon.io.joystick(0)(
      meltedMoon.io.joystick(0).high downto 1
    ) := 0x0
    meltedMoon.io.joystick(0).lsb := (
      RegNext(False, init=True)
    )


    meltedMoon.io.ddram.busy := (
      !(myPrng.io.outpXs.head % 10).orR
    )

    //val myFakeDdram = Mem(
    //  
    //)

    meltedMoon.io.ddram.dout := U(s"64'h123456789abcdef0")
    //meltedMoon.io.ddram.dout := myFakeDdram(
    //)
    meltedMoon.io.ddram.doutReady := (
      !meltedMoon.io.ddram.busy
      && (myPrng.io.outpXs.last % 10).orR
    )
  })

  meltedMoon.io.pllLocked := RegNext(True, init=False)
  meltedMoon.io.softReset(0) := (
    if (!cfg.dbgUseLcvBusMem) (
      meltedMoon.io.ioctl.download
    ) else (
      RegNext(False, init=True)
    )
  )
  meltedMoon.io.softReset(1) := (
    //RegNext(False, init=True)
    RegNext(RegNext(fell(meltedMoon.io.softReset(0))), init=False)
  )
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
  val sdramArea = (
    !cfg.dbgUseLcvBusMem
  ) generate new Area {
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
  }
  //--------
  //val mainClkDomain = ClockDomain.current
  //mainClkDomain.reset.simPublic()
  val needResetMainLogic = Bool()
  //meltedMoon.clockDomain.readResetWire := needResetMainLogic
  //meltedMoon.mainClkDomain.reset := needResetMainLogic
  //meltedMoon.io.mainLogicReset := needResetMainLogic
  val ioctlArea = (
    !cfg.dbgUseLcvBusMem
  ) generate new Area {
    val sdramInitRamInitBigInt = {
      //val depth = 1 << (16 - 4)
      //val tempArr = new ArrayBuffer[BigInt]()
      //tempArr ++= cfg.program.outpArr.view
      val program = SnowHouseRam32InitFromBin(
        filename=cfg.cpuCfg.programStr
      )

      //val myMemDepth = 0x4000
      val myMemInitBigInt = {
        //val depth = myMemDepth
        val tempArr = new ArrayBuffer[BigInt]()
        tempArr ++= program.view
        //while (tempArr.size < depth) {
        //  tempArr += BigInt(0)
        //}
        tempArr
      }

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
      //tempArr
      myMemInitBigInt
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
    val mySdramInitRdAddrStm = cloneOf(sdramInitRam.io.rdAddrPipe)

    //def myDelayCntMaxVal = (
    //  // BEGIN: try several values for debugging soft resets
    //  1024 * 8 * 8 * 8
    //  //1024 * 8 * 8 * 8 + 1
    //  //1024 * 8 * 8 * 8 + 2
    //  //1024 * 8 * 8 * 8 + 8
    //  //(1024 * 8 * 8 * 8)
    //  //1 << 22
    //  //1 << 16
    //  // END: try several values for debugging soft resets
    //)
    //val rMySdramInitRdAddrDelayCnt = (
    //  //Reg(SInt((log2Up(myDelayCntMaxVal + 1) + 2) bits))
    //  Reg(SInt(32 bits))
    //  init(myDelayCntMaxVal)
    //)
    //when (!rMySdramInitRdAddrDelayCnt.msb) {
    //  rMySdramInitRdAddrDelayCnt := rMySdramInitRdAddrDelayCnt - 1
    //}
    //sdramInitRam.io.rdAddrPipe << mySdramInitRdAddrStm.continueWhen(
    //  rMySdramInitRdAddrDelayCnt.msb
    //)
    sdramInitRam.io.rdAddrPipe << mySdramInitRdAddrStm
    when (mySdramInitRdAddrStm.fire) {
      rRamRdAddrCnt := rRamRdAddrCnt + 1
    }
    mySdramInitRdAddrStm.valid := (
      rRamRdAddrCnt(rRamRdAddrCnt.high downto 1)
      < sdramInitRamInitBigInt.size
    )
    mySdramInitRdAddrStm.addr := (
      rRamRdAddrCnt(rRamRdAddrCnt.high downto 1).resize(
        mySdramInitRdAddrStm.addr.getWidth
      )
    )
    mySdramInitRdAddrStm.data.data := 0x0
    mySdramInitRdAddrStm.data.addr := (
      //rRamRdAddrCnt(rRamRdAddrCnt.high downto 1).resize(
      //  mySdramInitRdAddrStm.data.addr.getWidth
      //)
      Cat(
        rRamRdAddrCnt,
        U"1'b0"
      ).asUInt.resize(
        mySdramInitRdAddrStm.data.addr.getWidth
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
    inSim=true,
    sdramCtrlUseAltddioOut=false,
    dbgExposeExtrasAtRegFileWrite=true,
    dbgUseLcvBusMem=true,
  )
  
  val numClkCycles: Long = 8192.toLong * 8 * 8 //* 8 * 8 * 8 * 8 * 8//2 //* 4//* 8 //* 4 * 8
  println(
    s"numClkCycles:${numClkCycles}"
  )
  val myCfg = Config.spinalExt(cfg.mainClkRate) 

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
      (((1 sec) / cfg.mainClkRate)) sec //ns //ms
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
      def myOutpChar = dut.io.outpChar //dut.cpuArea.nonIrqMmio.io.outpChar
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
    inSim=(
      //true
      false
    ),
    sdramCtrlUseAltddioOut=false,
    dbgUseLcvBusMem=(
      //false
      true
    ),
  )
  
  val numClkCycles = 8192 * 8 * 8 * 8 * 8 * 2//* 8 // * 8//2 //* 4//* 8 //* 4 * 8
  val myCfg = Config.spinalExt(cfg.mainClkRate) 

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
      (((1 sec) / cfg.mainClkRate)) sec //ns //ms
    )
    //dut.meltedMoon.ioctlClkDomain.forkStimulus(
    //  (((1 sec) / cfg.demoCfg.clkRate)) sec //ns //ms
    //)

    //dut.meltedMoon.vgaClkDomain.forkStimulus(
    //  //40
    //  //(((1 sec) / cfg.demoCfg.vgaTimingInfo.pixelClk)) sec //ns //ms
    //  //(((1 sec) / cfg.demoCfg.clkRate)) sec //ns //ms
    //  (((1 sec) / cfg.mainVgaClkRate)) sec //ns //ms
    //)
    for (i <- 0 until numClkCycles) {
      dut.clockDomain.waitSampling()
      ////dut.meltedMoon.ioctlClkDomain.waitSampling()
      //dut.meltedMoon.vgaClkDomain.waitSampling()
      ////dut.clockDomain.readResetWire #= dut.io.needResetMainLogic.toBoolean
      ////var tickVgaClk: Boolean = false
      ////if (
      ////  (
      ////    i
      ////    % (
      ////      //demoCfg.vgaTimingInfo.pixelClk / (1.0 MHz)
      ////      demoCfg.clkRate / demoCfg.vgaTimingInfo.pixelClk /// (1.0 MHz)
      ////    )
      ////  ) == 0
      ////) {
      ////  tickVgaClk = true
      ////  dut.vgaClkDomain.waitSampling()
      ////}
      ////println(
      ////  s"i:${i}, tickVgaClk:${tickVgaClk}"
      ////)
    }
  }}
}

object MeltedMoonDebugToVerilog extends App {
  val cfg = MeltedMoonConfig(
    inSim=true,
    sdramCtrlUseAltddioOut=false,
    dbgExposeExtrasAtRegFileWrite=true,
    dbgUseLcvBusMem=(
      //true
      false
    ),
  )
  Config.spinalExt(
    clkRate=cfg.mainClkRate,
    targetDirectory="hw/gen/meltedMoonDebug",
    //oneFilePerComponent=true,
  ).generateVerilog{
    //MeltedMoon(cfg=cfg)
    MeltedMoonSimDut(cfg=cfg)
  }
}

object MeltedMoonToVerilog extends App {
  val cfg = MeltedMoonConfig(
    inSim=false,
    sdramCtrlUseAltddioOut=(
      true
    ),
  )
  Config.spinalExt(
    clkRate=cfg.mainClkRate,
    //targetDirectory="hw/gen/meltedMoonDebug",
    //resetKind=(
    //  SYNC
    //  //ASYNC
    //  //BOOT
    //),
  ).generateVerilog{
    MeltedMoon(cfg=cfg)
    //MeltedMoonFbDdram(cfg=cfg)
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

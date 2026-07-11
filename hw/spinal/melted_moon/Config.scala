package melted_moon

import spinal.core._
import spinal.core.sim._

//object Config {
//  def spinal = SpinalConfig(
//    targetDirectory = "hw/gen",
//    defaultConfigForClockDomains = ClockDomainConfig(
//      resetKind = BOOT,
//      resetActiveLevel = HIGH
//    ),
//    onlyStdLogicVectorAtTopLevelIo = true
//  )
//
//  def sim = SimConfig.withConfig(spinal).withFstWave
//}
object Config {
  //def spinal = SpinalConfig(
  //  targetDirectory = "hw/gen",
  //  defaultConfigForClockDomains = ClockDomainConfig(
  //    resetActiveLevel = HIGH
  //  ),
  //  onlyStdLogicVectorAtTopLevelIo = true
  //)
  def spinalExt(
    clkRate: HertzNumber,
    targetDirectory: String="hw/gen",
    //isMister: Boolean=false,
    resetKind: ResetKind=SYNC,
    oneFilePerComponent: Boolean=false,
  ) = (
    SpinalConfig(
      targetDirectory=targetDirectory,
      defaultConfigForClockDomains=ClockDomainConfig(
        resetActiveLevel=HIGH,
        resetKind=resetKind,
      ),
      formalAsserts=true,
      defaultClockDomainFrequency=FixedFrequency(clkRate),
      oneFilePerComponent=oneFilePerComponent,
    )
      //.addStandardMemBlackboxing(blackboxAllWhatsYouCan)
  )

  def simExt(
    clkRate: HertzNumber,
    targetDirectory: String="hw/gen",
    withFstWave: Boolean=true,
  ) = {
    val ret = SimConfig.withConfig(spinalExt(
      clkRate=clkRate,
      targetDirectory=targetDirectory,
    ))
    if (withFstWave) (
      ret.withFstWave
    ) else (
      ret
    )
  }

  //def sim = SimConfig.withConfig(spinal).withFstWave

  def spinal = (
    SpinalConfig(
      targetDirectory="hw/gen",
      defaultConfigForClockDomains=ClockDomainConfig(
        resetActiveLevel=HIGH,
        resetKind=SYNC,
      ),
      formalAsserts=true,
      defaultClockDomainFrequency=FixedFrequency(100.0 MHz),
    )
      //.addStandardMemBlackboxing(blackboxAllWhatsYouCan)
  )
  def simWithCfg(
    cfg: SpinalConfig,
    withFstWave: Boolean=true,
  ) = {
    val ret = SimConfig.withConfig(cfg)
    if (withFstWave) (
      ret.withFstWave
    ) else (
      ret
    )
  }
  def sim = simWithCfg(spinal)

  //def spinalFormal = SpinalFormalConfig(
  //  _spinalConfig=spinal,
  //  _keepDebugInfo=true,
  //)
}

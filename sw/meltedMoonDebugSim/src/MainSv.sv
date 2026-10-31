`ifdef STRICT_SDRAM
`timescale 1ns/1ps
`endif

module MainSv (
  output wire [7:0]    vgaPhys_col_r,
  output wire [7:0]    vgaPhys_col_g,
  output wire [7:0]    vgaPhys_col_b,
  output wire          vgaPhys_hsync,
  output wire          vgaPhys_vsync,
  output wire          vgaPixelEn,
  output wire          vgaVisib,
  output wire [31:0]   cpuDbgInfo_regFileWriteData,
  output wire [4:0]    cpuDbgInfo_regFileWriteAddr,
  output wire          cpuDbgInfo_regFileWriteEnable,
  output wire [31:0]   cpuDbgInfo_laggingRegPcAtRegFileWrite,
  output wire          cpuDbgInfo_myPsIdBubbleAtRegFileWrite,
  output wire          cpuDbgInfo_shouldIgnoreInstrAtRegFileWrite,
  output wire [31:0]   cpuDbgInfo_encInstrAtRegFileWrite,
  output wire [31:0]   cpuDbgInfo_immAtRegFileWrite,
  output wire [31:0]   cpuDbgInfo_rdMemWordAtRegFileWrite_0,
  output wire [31:0]   cpuDbgInfo_rdMemWordAtRegFileWrite_1,
  output wire [4:0]    cpuDbgInfo_gprIdxVecAtRegFileWrite_0,
  output wire [4:0]    cpuDbgInfo_gprIdxVecAtRegFileWrite_1,
  output wire [4:0]    cpuDbgInfo_gprIdxVecAtRegFileWrite_2,
  output wire [3:0]    cpuDbgInfo_reorderBufIdx,
  //output wire          regFileWriteActive,
  output wire          outpChar_valid,
  output wire [7:0]    outpChar_payload,
  output wire [31:0]   dbgIo_dcacheDbgInfo_hit_0_cnt,
  output wire [31:0]   dbgIo_dcacheDbgInfo_hit_0_addr,
  output wire          dbgIo_dcacheDbgInfo_hit_0_isWrite,
  output wire [2:0]    dbgIo_dcacheDbgInfo_hit_0_ramIdx,
  output wire [31:0]   dbgIo_dcacheDbgInfo_hit_1_cnt,
  output wire [31:0]   dbgIo_dcacheDbgInfo_hit_1_addr,
  output wire          dbgIo_dcacheDbgInfo_hit_1_isWrite,
  output wire [2:0]    dbgIo_dcacheDbgInfo_hit_1_ramIdx,
  output wire [31:0]   dbgIo_dcacheDbgInfo_missSend_cnt,
  output wire [31:0]   dbgIo_dcacheDbgInfo_missSend_addr,
  output wire          dbgIo_dcacheDbgInfo_missSend_isWrite,
  output wire [2:0]    dbgIo_dcacheDbgInfo_missSend_ramIdx,
  output wire [31:0]   dbgIo_dcacheDbgInfo_missRecv_cnt,
  output wire [31:0]   dbgIo_dcacheDbgInfo_missRecv_addr,
  output wire          dbgIo_dcacheDbgInfo_missRecv_isWrite,
  output wire [2:0]    dbgIo_dcacheDbgInfo_missRecv_ramIdx,

  `ifdef STRICT_SDRAM
  output 
  `else
  input
  `endif    // STRICT_SDRAM
  logic                clk,

  output logic         reset
);
    int unsigned my_rst_cnt;

    initial begin
        `ifdef STRICT_SDRAM
        clk = 1'b0;
        `endif  // STRICT_SDRAM
        reset = 1'b1;
        my_rst_cnt = 10;
    end

    `ifdef STRICT_SDRAM
    localparam realtime CLK_PERIOD_NS = 1000.0 / 98.0;

    always begin
        //#5ns
        //#5.434782608695652  // 92 MHz,
                            // need to flip `clk` at (1/(92 * 2) MHz)! 
        //#5
        #(CLK_PERIOD_NS / 2.0)
        clk = !clk;
    end
    `endif      // STRICT_SDRAM

    always_ff @(posedge clk) begin
        if (my_rst_cnt != 0) begin
            my_rst_cnt <= my_rst_cnt - 'd1;
        end else begin
            reset <= 1'b0;
        end
    end

    assign dbgIo_dcacheDbgInfo_hit_0_cnt = 'h0;
    assign dbgIo_dcacheDbgInfo_hit_0_addr = 'h0;
    assign dbgIo_dcacheDbgInfo_hit_0_isWrite = 'h0;
    assign dbgIo_dcacheDbgInfo_hit_0_ramIdx = 'h0;
    assign dbgIo_dcacheDbgInfo_hit_1_cnt = 'h0;
    assign dbgIo_dcacheDbgInfo_hit_1_addr = 'h0;
    assign dbgIo_dcacheDbgInfo_hit_1_isWrite = 'h0;
    assign dbgIo_dcacheDbgInfo_hit_1_ramIdx = 'h0;
    assign dbgIo_dcacheDbgInfo_missSend_cnt = 'h0;
    assign dbgIo_dcacheDbgInfo_missSend_addr = 'h0;
    assign dbgIo_dcacheDbgInfo_missSend_isWrite = 'h0;
    assign dbgIo_dcacheDbgInfo_missSend_ramIdx = 'h0;
    assign dbgIo_dcacheDbgInfo_missRecv_cnt = 'h0;
    assign dbgIo_dcacheDbgInfo_missRecv_addr = 'h0;
    assign dbgIo_dcacheDbgInfo_missRecv_isWrite = 'h0;
    assign dbgIo_dcacheDbgInfo_missRecv_ramIdx = 'h0;

    MeltedMoonSimDut dut(
        .vgaPhys_col_r,
        .vgaPhys_col_g,
        .vgaPhys_col_b,
        .vgaPhys_hsync,
        .vgaPhys_vsync,
        .vgaPixelEn,
        .vgaVisib,
        .cpuDbgInfo_regFileWriteData,
        .cpuDbgInfo_regFileWriteAddr,
        .cpuDbgInfo_regFileWriteEnable,
        .cpuDbgInfo_laggingRegPcAtRegFileWrite,
        .cpuDbgInfo_myPsIdBubbleAtRegFileWrite,
        .cpuDbgInfo_shouldIgnoreInstrAtRegFileWrite,
        .cpuDbgInfo_encInstrAtRegFileWrite,
        .cpuDbgInfo_immAtRegFileWrite,
        .cpuDbgInfo_rdMemWordAtRegFileWrite_0,
        .cpuDbgInfo_rdMemWordAtRegFileWrite_1,
        .cpuDbgInfo_gprIdxVecAtRegFileWrite_0,
        .cpuDbgInfo_gprIdxVecAtRegFileWrite_1,
        .cpuDbgInfo_gprIdxVecAtRegFileWrite_2,
        .cpuDbgInfo_reorderBufIdx,
        //.regFileWriteActive,
        .outpChar_valid,
        .outpChar_payload,
        //.dbgIo_dcacheDbgInfo_hit_0_cnt,
        //.dbgIo_dcacheDbgInfo_hit_0_addr,
        //.dbgIo_dcacheDbgInfo_hit_0_isWrite,
        //.dbgIo_dcacheDbgInfo_hit_0_ramIdx,
        //.dbgIo_dcacheDbgInfo_hit_1_cnt,
        //.dbgIo_dcacheDbgInfo_hit_1_addr,
        //.dbgIo_dcacheDbgInfo_hit_1_isWrite,
        //.dbgIo_dcacheDbgInfo_hit_1_ramIdx,
        //.dbgIo_dcacheDbgInfo_missSend_cnt,
        //.dbgIo_dcacheDbgInfo_missSend_addr,
        //.dbgIo_dcacheDbgInfo_missSend_isWrite,
        //.dbgIo_dcacheDbgInfo_missSend_ramIdx,
        //.dbgIo_dcacheDbgInfo_missRecv_cnt,
        //.dbgIo_dcacheDbgInfo_missRecv_addr,
        //.dbgIo_dcacheDbgInfo_missRecv_isWrite,
        //.dbgIo_dcacheDbgInfo_missRecv_ramIdx,
        .clk,
        .reset
    );

endmodule

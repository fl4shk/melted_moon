`default_nettype none
module melted_moon
(
	input         clk,
	//input         vgaClk_clk,
	//input         clk_cpu,
	input         reset,
	
	input         pal,
	input         scandouble,
	input         pll_locked,

  output wire  [7:0]   vgaPhys_col_r,
  output wire  [7:0]   vgaPhys_col_g,
  output wire  [7:0]   vgaPhys_col_b,
  output wire          vgaPhys_hsync,
  output wire          vgaPhys_vsync,
  output wire          vgaPixelEn,
  output wire          vgaVisib,

	//output reg    ce_pix,

	//output wire    HBlank,
	//output wire    HSync,
	//output wire    VBlank,
	//output wire    VSync,
	//input [1:0]   buttons,

	//output  [7:0] video,
	//output [23:0] video

	// ARM -> FPGA download
	input         ioctl_download,
	  // signal indicating an active download
	input [15:0]  ioctl_index,
	  // menu index used to upload the file
	input         ioctl_wr,
	input [26:0]  ioctl_addr,
	  // in WIDE mode address will be incremented by 2
	input [15:0]  ioctl_dout,
	input         ioctl_upload,
	  // signal indicating an active upload

	output        ioctl_upload_req,
	  // request to save (must be supported on HPS side for specific core)
	output [7:0]  ioctl_upload_index,
	output [15:0] ioctl_din,
	input         ioctl_rd,
	input [31:0]  ioctl_file_ext,
	output        ioctl_wait,

	output        sdram_CLK,
	output        sdram_CKE,
	output [12:0] sdram_A,
	output  [1:0] sdram_BA,
	inout  [15:0] sdram_DQ,
	output        sdram_DQML,
	output        sdram_DQMH,
	output        sdram_nCS,
	output        sdram_nCAS,
	output        sdram_nRAS,
	output        sdram_nWE
);

//reg   [9:0] hc;
//reg   [9:0] vc;
//reg   [9:0] vvc;
//reg  [63:0] rnd_reg, rnd_reg1;
//
//wire  [5:0] rnd_c = {rnd_reg[0],rnd_reg[1],rnd_reg[2],rnd_reg[2],rnd_reg[2],rnd_reg[2]};
//wire [63:0] rnd;

//wire cpu_io_idsIraIrq_ready;
//wire [31:0] cpu_io_regFileWriteData;
//reg [0:0] cpu_io_regFileWriteData_sys_reg;
//reg [0:0] cpu_io_regFileWriteData_sys_reg1;
//reg [0:0] cpu_io_regFileWriteData_sys_reg2;
//
//reg [0:0] cpu_io_regFileWriteData_cpu_reg;
//reg [0:0] cpu_io_regFileWriteData_cpu_reg1;
//reg [0:0] cpu_io_regFileWriteData_cpu_reg2;

//reg [1:0] buttons_reg = 0;

//reg pal_sys_reg; //= 1'b0;
//reg pal_sys_reg1; //= 1'b0;
//reg pal_sys_reg2; //= 1'b0
//
//reg pal_cpu_reg; //= 1'b0;
//reg pal_cpu_reg1; //= 1'b0;
//reg pal_cpu_reg2; //= 1'b0

//localparam SOFT_RESET_CNT_WIDTH = 17;
//localparam SOFT_RESET_CNT_MSB_POS = SOFT_RESET_CNT_WIDTH - 1;
//reg [SOFT_RESET_CNT_MSB_POS:0] soft_reset_cnt = (
//  1 << SOFT_RESET_CNT_MSB_POS
//);

//reg soft_reset_sys_reg = 1'b0;
//reg soft_reset_sys_reg1 = 1'b0;
//reg soft_reset_sys_reg2 = 1'b0;
////reg prev_soft_reset_cpu_reg = 1'b0;
//reg soft_reset_cpu_reg = 1'b0; //= 1'b0;
//reg soft_reset_cpu_reg1 = 1'b0; //= 1'b0;
//reg soft_reset_cpu_reg2 = 1'b0; //= 1'b0;

reg reset_sys_reg = 1'b1;
reg reset_sys_reg1 = 1'b1;
reg reset_sys_reg2 = 1'b1;
reg reset_cpu_reg = 1'b1; //= 1'b0;
reg reset_cpu_reg1 = 1'b1; //= 1'b0;
reg reset_cpu_reg2 = 1'b1; //= 1'b0;

//always @(posedge clk) begin
//	if (reset) begin
//		pal_sys_reg2 <= 1'b0;
//		pal_sys_reg1 <= 1'b0;
//		pal_sys_reg <= 1'b0;
//		cpu_io_regFileWriteData_sys_reg2 <= 32'd0;
//		cpu_io_regFileWriteData_sys_reg1 <= 32'd0;
//		cpu_io_regFileWriteData_sys_reg <= 32'd0;
//	end else begin
//		pal_sys_reg2 <= pal;
//		pal_sys_reg1 <= pal_sys_reg2;
//		pal_sys_reg <= pal_sys_reg1;
//		cpu_io_regFileWriteData_sys_reg2 <= cpu_io_regFileWriteData_cpu_reg;
//		cpu_io_regFileWriteData_sys_reg1 <= cpu_io_regFileWriteData_sys_reg2;
//		cpu_io_regFileWriteData_sys_reg <= cpu_io_regFileWriteData_sys_reg1;
//	end
//end
//
//wire please_do_soft_reset = !soft_reset_cnt[SOFT_RESET_CNT_MSB_POS];

//always @(posedge clk) begin
//  //if (pll_locked) begin
//    soft_reset_sys_reg2 <= reset || ioctl_download;//1'b0;//reset;
//    soft_reset_sys_reg1 <= soft_reset_sys_reg2;
//    soft_reset_sys_reg <= soft_reset_sys_reg1;
//  //end
//end
//always @ (posedge clk/*clk_cpu*/) begin
//  //if (pll_locked) begin
//    soft_reset_cpu_reg2 <= soft_reset_sys_reg;//reset;//reset_sys_reg;
//    soft_reset_cpu_reg1 <= soft_reset_cpu_reg2;
//    soft_reset_cpu_reg <= soft_reset_cpu_reg1;
//    //prev_soft_reset_cpu_reg <= soft_reset_cpu_reg;
//    //reset_cpu_reg1 <= reset;
//    //reset_cpu_reg <= reset;//reset_cpu_reg1; //reset;
//    //if (
//    //  //!please_do_soft_reset
//    //  //&& 
//    //  soft_reset_cpu_reg
//    //  //&& !prev_soft_reset_cpu_reg
//    //) begin
//    //  soft_reset_cnt <= 0;
//    //end else if (
//    //  //!soft_reset_cnt[SOFT_RESET_CNT_MSB_POS]
//    //  please_do_soft_reset
//    //) begin
//    //  soft_reset_cnt <= soft_reset_cnt + 1;
//    //end
//  //end
//end
wire my_soft_reset_0 = (
  //reset || 
  ioctl_download
);
wire my_soft_reset_1 = (
  reset
);


always @(posedge clk) begin
  if (pll_locked) begin
    reset_sys_reg2 <= 1'b0;//reset;//(reset || my_soft_reset_0);//1'b0;//reset;
  end
  reset_sys_reg1 <= reset_sys_reg2;
  reset_sys_reg <= reset_sys_reg1;
end
always @ (posedge clk/*clk_cpu*/) begin
  //if (pll_locked) begin
    reset_cpu_reg2 <= reset_sys_reg;//reset;//reset_sys_reg;
    reset_cpu_reg1 <= reset_cpu_reg2;
    reset_cpu_reg <= reset_cpu_reg1;
    //reset_cpu_reg1 <= reset;
    //reset_cpu_reg <= reset;//reset_cpu_reg1; //reset;
  //end
end

wire [12:0] temp_sdram_a;
//wire temp_sdram_dqmh;
//wire temp_sdram_dqml;
//assign sdram_A = {temp_sdram_dqmh, temp_sdram_dqml, temp_sdram_a[10:0]};
//assign sdram_DQMH = temp_sdram_dqmh;
//assign sdram_DQML = temp_sdram_dqml;
// NOTE: MiSTer-specific SDRAM board DQM stuff
assign {sdram_DQMH, sdram_DQML} = temp_sdram_a[12:11];
assign sdram_A = temp_sdram_a;
wire temp_ioctl_myWait;
assign ioctl_wait = (
  //|({
    //reset,
    //reset_sys_reg2,
    //reset_sys_reg1,
    //reset_sys_reg,
    //reset_cpu_reg2,
    //reset_cpu_reg1,
    //reset_cpu_reg,

    //soft_reset_sys_reg2,
    //soft_reset_sys_reg1,
    //soft_reset_sys_reg,
    //soft_reset_cpu_reg2,
    //soft_reset_cpu_reg1,
    //soft_reset_cpu_reg,
    temp_ioctl_myWait
  //})
);

MeltedMoon myMeltedMoon(
  .pllLocked(pll_locked),
  .sdram_dq(sdram_DQ),
  .sdram_a(temp_sdram_a),
  //.sdram_dqml(temp_sdram_dqml),
  //.sdram_dqmh(temp_sdram_dqmh),
  .sdram_ba(sdram_BA),
  .sdram_nCs(sdram_nCS),
  .sdram_nWe(sdram_nWE),
  .sdram_nRas(sdram_nRAS),
  .sdram_nCas(sdram_nCAS),
  .sdram_cke(sdram_CKE),
  .sdram_clk(sdram_CLK),
  .ioctl_download(ioctl_download),
  .ioctl_index(ioctl_index),
  .ioctl_wr(ioctl_wr),
  .ioctl_addr(ioctl_addr),
  .ioctl_dout(ioctl_dout),
  .ioctl_upload(ioctl_upload),
  .ioctl_upload_req(ioctl_upload_req),
  .ioctl_upload_index(ioctl_upload_index),
  .ioctl_din(ioctl_din),
  .ioctl_rd(ioctl_rd),
  .ioctl_file_ext(ioctl_file_ext),
  .ioctl_myWait(
    temp_ioctl_myWait
    //ioctl_wait
  ),
  .vgaPhys_col_r(vgaPhys_col_r),
  .vgaPhys_col_g(vgaPhys_col_g),
  .vgaPhys_col_b(vgaPhys_col_b),
  .vgaPhys_hsync(vgaPhys_hsync),
  .vgaPhys_vsync(vgaPhys_vsync),
  .vgaPixelEn(vgaPixelEn),
  .vgaVisib(vgaVisib),
  .clk(
    //my_clk
    clk
  ),
  //.vgaClk_clk(my_vgaClk_clk),
  //.vgaClk_reset(my_vgaClk_reset),
  .reset(
    //my_reset
    //reset_sys_reg
    reset_cpu_reg
  ),
  .softReset_0(
    my_soft_reset_0
    //soft_reset_cpu_reg
    //|| please_do_soft_reset
  ),
  .softReset_1(
    my_soft_reset_1
    //soft_reset_cpu_reg
    //|| please_do_soft_reset
  )
);

//always @(posedge clk) begin
//	if(scandouble) ce_pix <= 1;
//		else ce_pix <= ~ce_pix;
//
//	if(reset) begin
//		hc <= 0;
//		vc <= 0;
//	end
//	else if(ce_pix) begin
//		if(hc == 637) begin
//			hc <= 0;
//			if(vc == (pal ? (scandouble ? 623 : 311) : (scandouble ? 523 : 261))) begin 
//				vc <= 0;
//				vvc <= vvc + 9'd6;
//			end else begin
//				vc <= vc + 1'd1;
//			end
//		end else begin
//			hc <= hc + 1'd1;
//		end
//
//		//rnd1 <= rnd;
//		//rnd_reg1 <= {32'd0, cpu_io_regFileWriteData_sys_reg}; //{cpu_io_regFileWriteData, rnd[63:32]};
//		rnd_reg1 <= rnd;
//		rnd_reg <= rnd_reg1;
//	end
//end
//
//always @(posedge clk) begin
//	if (hc == 529) HBlank <= 1;
//		else if (hc == 0) HBlank <= 0;
//
//	if (hc == 544) begin
//		HSync <= 1;
//
//		if(pal) begin
//			if(vc == (scandouble ? 609 : 304)) VSync <= 1;
//				else if (vc == (scandouble ? 617 : 308)) VSync <= 0;
//
//			if(vc == (scandouble ? 601 : 300)) VBlank <= 1;
//				else if (vc == 0) VBlank <= 0;
//		end
//		else begin
//			if(vc == (scandouble ? 490 : 245)) VSync <= 1;
//				else if (vc == (scandouble ? 496 : 248)) VSync <= 0;
//
//			if(vc == (scandouble ? 480 : 240)) VBlank <= 1;
//				else if (vc == 0) VBlank <= 0;
//		end
//	end
//	
//	if (hc == 590) HSync <= 0;
//end
//
//reg  [7:0] cos_out;
//wire [5:0] cos_g = cos_out[7:3]+6'd32;
//cos cos(vvc + {vc>>scandouble, 2'b00}, cos_out);
//
//assign video = (cos_g >= rnd_c) ? {cos_g - rnd_c, 2'b00} : 8'd0;

endmodule

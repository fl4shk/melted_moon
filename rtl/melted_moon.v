`default_nettype none

module melted_moon_clk_domain_reset(
  //--------
  input     clk_50m,
  input     pll_locked,
  //--------
  input     clk_main,
  output    reset_main
  //--------
);

reg my_locked_reg_del1;
reg my_locked_reg;
reg my_locked_reg1;
reg my_locked_reg2;

reg reset_temp_reg_del1;
reg reset_temp_reg;
reg reset_temp_reg1;
reg reset_temp_reg2;

reg reset_main_reg;
reg reset_main_reg1;
reg reset_main_reg2;

localparam PLL_LOCKED_CNT_WIDTH = 5;
reg [PLL_LOCKED_CNT_WIDTH - 1:0] pll_locked_cnt; //= 'd0;
wire pll_locked_cnt_done = pll_locked_cnt[PLL_LOCKED_CNT_WIDTH - 1];

always @(posedge clk_50m) begin
  my_locked_reg2 <= pll_locked;
  my_locked_reg1 <= my_locked_reg2;
  my_locked_reg <= my_locked_reg1;
  my_locked_reg_del1 <= my_locked_reg;
end

always @(posedge clk_50m) begin
  if (my_locked_reg && !my_locked_reg_del1) begin
    pll_locked_cnt <= 'd0;
  end else if (!pll_locked_cnt_done) begin
    pll_locked_cnt <= pll_locked_cnt + 'd1;
  end
end

//wire my_cpu_clk = clk_sys; //clk_50m; //clk_sys;

reg my_reset_reg_del1;
reg my_reset_reg;
reg my_reset_reg1;
reg my_reset_reg2;
reg my_reset_done;

always @(posedge clk_main) begin
  my_reset_reg2 <= pll_locked_cnt_done;
  my_reset_reg1 <= my_reset_reg2;
  my_reset_reg <= my_reset_reg1;
  my_reset_reg_del1 <= my_reset_reg;
  if (my_reset_reg && !my_reset_reg_del1) begin
    my_reset_done <= 1'b1;
  end else begin
    my_reset_done <= 1'b0;
  end
end

//always @(posedge clk_main) begin
//  if (my_reset_done[1] && my_reset_done[2]) begin
//  end
//end

always @(posedge clk_main) begin
  //if (pll_locked_cnt_done) begin
  //reset_temp_reg2 <= 1'b0;//pll_locked_cnt_done; //my_locked_reg;
  //end
  reset_temp_reg2 <= my_reset_done;// && my_reset_done[1];
  reset_temp_reg1 <= reset_temp_reg2;
  reset_temp_reg <= reset_temp_reg1;
  reset_temp_reg_del1 <= reset_temp_reg;
end
always @ (posedge clk_main) begin
  //reset_main_reg2 <= reset_temp_reg;//reset;//reset_temp_reg;
  if (reset_temp_reg && !reset_temp_reg_del1) begin
    reset_main_reg2 <= 1'b1;
    reset_main_reg1 <= 1'b1;
    reset_main_reg <= 1'b1;
  end else begin
    reset_main_reg2 <= 1'b0;
    reset_main_reg1 <= reset_main_reg2;
    reset_main_reg <= reset_main_reg1;
  end

  //reset_main_reg1 <= reset;
  //reset_main_reg <= reset;//reset_main_reg1; //reset;
end

assign reset_main = reset_main_reg;

endmodule

module melted_moon(
  input         clk_50m,
	input         clk_sys,
	//input         clk_vga,
	//input         vgaClk_clk,
	//input         clk_cpu,
	input         reset,

	input        [31:0] joystick_0,
	input        [31:0] joystick_1,
	input        [31:0] joystick_2,
	input        [31:0] joystick_3,
	input        [31:0] joystick_4,
	input        [31:0] joystick_5,
	
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

  output wire          ddram_clk,
  input  wire          ddram_busy,
  output wire [7:0]    ddram_burstCnt,
  output wire [28:0]   ddram_addr,
  input  wire [63:0]   ddram_dout,
  input  wire          ddram_doutReady,
  output wire          ddram_rd,
  output wire [63:0]   ddram_din,
  output wire [7:0]    ddram_be,
  output wire          ddram_we,

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



wire reset_cpu;
//wire reset_vga;

melted_moon_clk_domain_reset cpu_clk_domain_reset(
  .clk_50m(clk_50m),
  .pll_locked(pll_locked),
  .clk_main(clk_sys),
  .reset_main(reset_cpu)
);

//melted_moon_clk_domain_reset vga_clk_domain_reset(
//  .clk_50m(clk_50m),
//  .pll_locked(pll_locked),
//  .clk_main(clk_vga),
//  .reset_main(reset_vga)
//);

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
assign ioctl_wait = temp_ioctl_myWait;

wire my_soft_reset_0 = (
  //reset || 
  ioctl_download
);
wire my_soft_reset_1 = (
  reset
);


MeltedMoon myMeltedMoon(
  .pllLocked(pll_locked),

  .joystick_0(joystick_0),
  .joystick_1(joystick_1),
  .joystick_2(joystick_2),
  .joystick_3(joystick_3),
  .joystick_4(joystick_4),
  .joystick_5(joystick_5),

  .ddram_clk(ddram_clk),
  .ddram_busy(ddram_busy),
  .ddram_burstCnt(ddram_burstCnt),
  .ddram_addr(ddram_addr),
  .ddram_dout(ddram_dout),
  .ddram_doutReady(ddram_doutReady),
  .ddram_rd(ddram_rd),
  .ddram_din(ddram_din),
  .ddram_be(ddram_be),
  .ddram_we(ddram_we),

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
    clk_sys
    //my_cpu_clk
  ),
  //.vgaClk_clk(
  //  clk_vga
  //),
  //.vgaClk_reset(
  //  reset_vga
  //),
  .reset(
    //my_reset
    //reset_sys_reg
    //reset_cpu_reg
    reset_cpu
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

//always @(posedge clk_sys) begin
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
//always @(posedge clk_sys) begin
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

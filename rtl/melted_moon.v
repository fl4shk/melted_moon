module melted_moon
(
	input         clk,
	//input         vgaClk_clk,
	//input         clk_cpu,
	input         reset,
	
	input         pal,
	input         scandouble,

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

reg reset_sys_reg = 1'b0;
reg reset_sys_reg1 = 1'b0;
reg reset_sys_reg2 = 1'b0;
reg reset_cpu_reg = 1'b0; //= 1'b0;
reg reset_cpu_reg1 = 1'b0; //= 1'b0;
reg reset_cpu_reg2 = 1'b0; //= 1'b0;

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

always @(posedge clk) begin
  reset_sys_reg2 <= reset;
  reset_sys_reg1 <= reset_sys_reg2;
  reset_sys_reg <= reset_sys_reg1;
end
always @ (posedge clk/*clk_cpu*/) begin
  reset_cpu_reg2 <= reset_sys_reg;
  reset_cpu_reg1 <= reset_cpu_reg2;
  reset_cpu_reg <= reset_cpu_reg1;
end

//always @(posedge clk_cpu) begin
//  if (reset_cpu_reg) begin
//    pal_cpu_reg2 <= 1'b0;
//    pal_cpu_reg1 <= 1'b0;
//    pal_cpu_reg <= 1'b0;
//    cpu_io_regFileWriteData_cpu_reg2 <= 32'd0;
//    cpu_io_regFileWriteData_cpu_reg1 <= 32'd0;
//    cpu_io_regFileWriteData_cpu_reg <= 32'd0;
//  end else begin
//    pal_cpu_reg2 <= pal_sys_reg;
//    pal_cpu_reg1 <= pal_cpu_reg2;
//    pal_cpu_reg <= pal_cpu_reg1;
//    cpu_io_regFileWriteData_cpu_reg2 <= cpu_io_regFileWriteData[0];
//    cpu_io_regFileWriteData_cpu_reg1 <= cpu_io_regFileWriteData_cpu_reg2;
//    cpu_io_regFileWriteData_cpu_reg <= cpu_io_regFileWriteData_cpu_reg1;
//  end
//end

//lfsr random(
//	.clk(clk),
//	.reset(reset),
//	.rnd(rnd)
//);

//assign rnd[63:33] = rnd_inp[63:33];
//SnowHouseCpuWithDualRam cpu_etc(
//	.io_idsIraIrq_nextValid(pal_cpu_reg),
//	.io_idsIraIrq_ready(cpu_io_idsIraIrq_ready),
//	.io_regFileWriteData(cpu_io_regFileWriteData),
//	.reset(reset_cpu_reg),
//	.clk(clk_cpu)
//);
//LcvBusNonCoherentDataCacheWithSdramCtrl cacheTesterWithSdramCtrl(
//  .io_dq(sdram_DQ),
//  .io_a(sdram_A),
//  .io_dqml(sdram_DQML),
//  .io_dqmh(sdram_DQMH),
//  .io_ba(sdram_BA),
//  .io_nCs(sdram_nCS),
//  .io_nWe(sdram_nWE),
//  .io_nRas(sdram_nRAS),
//  .io_nCas(sdram_nCAS),
//  .io_cke(sdram_CKE),
//  .io_clk(sdram_CLK),
//  .clk(clk)//,
//  //.reset(reset_cpu_reg)
//);
wire my_clk = clk;
reg my_reset = 1'b1;
always @(posedge my_clk) begin
  my_reset = 1'b0;
end

wire my_vgaClk_clk = clk;//vgaClk_clk;//clk;
reg my_vgaClk_reset = 1'b1;
always @(posedge my_vgaClk_clk) begin
  my_vgaClk_reset = 1'b0;
end

MeltedMoon myMeltedMoon(
  .mainLogicReset(ioctl_wr || ioctl_download),
  .sdram_dq(sdram_DQ),
  .sdram_a(sdram_A),
  .sdram_dqml(sdram_DQML),
  .sdram_dqmh(sdram_DQMH),
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
  .ioctl_wait(ioctl_wait),
  .vgaPhys_col_r(vgaPhys_col_r),
  .vgaPhys_col_g(vgaPhys_col_g),
  .vgaPhys_col_b(vgaPhys_col_b),
  .vgaPhys_hsync(vgaPhys_hsync),
  .vgaPhys_vsync(vgaPhys_vsync),
  .vgaPixelEn(vgaPixelEn),
  .vgaVisib(vgaVisib),
  .clk(my_clk),
  .vgaClk_clk(my_vgaClk_clk),
  .vgaClk_reset(my_vgaClk_reset),
  .reset(my_reset)
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

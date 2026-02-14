module lfsr(
   input wire clk,
   input wire reset,
   output reg [N - 1:0] rnd
);

parameter N = 63;

wire [N - 1:0] rnd1;
reg [N - 1:0] rnd1_reg;

lcell lc0(~(rnd1[N - 1] ^ rnd1[N - 3] ^ rnd1[N - 4] ^ rnd1[N - 6] ^ rnd1[N - 10]), rnd1[0]);
generate 
	genvar i;
	for (i = 0; i <= N - 2; i = i + 1) begin : lcn
		lcell lc(rnd1[i], rnd1[i + 1]);
	end
endgenerate

always @(posedge clk) begin
	if (reset) begin
		rnd1_reg <= 0;
		rnd <= 0;
	end else begin
		rnd1_reg <= rnd1;
		rnd <= rnd1_reg;
	end
end
endmodule

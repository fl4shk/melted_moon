//#include "config.h"

#include <iostream>
#include <memory>
#include <string>
#include <vector>
#include <cstring>
#include <cstdio>
//#include <SDL.h>
//#include <SDL_video.h>
//#include <SDL_render.h>
//#include <SDL_syswm.h>
#include "liborangepower_src/game_stuff/engine_key_status_class.hpp"
#include "liborangepower_src/math/vec2_classes.hpp"
//#include "liborangepower_src/misc/misc_types.hpp"
#include "liborangepower_src/misc/misc_includes.hpp"
#include "liborangepower_src/sdl2/sdl.hpp"
#include "liborangepower_src/sdl2/dpi_stuff.hpp"
#include "liborangepower_src/sdl2/keyboard_stuff.hpp"
#include "liborangepower_src/sdl2/sdl_video.hpp"
#include "liborangepower_src/sdl2/sdl_render.hpp"
#include "liborangepower_src/sdl2/sdl_surface.hpp"
#include "liborangepower_src/sdl2/sdl_rect.hpp"
#include <SDL_events.h>
#include "liborangepower_src/misc/misc_output_funcs.hpp"
//#include "VMeltedMoon.h"
//#include "VMeltedMoon.h"
#include "VMeltedMoonSimDut.h"
#include "verilated_vcd_c.h"
#include "verilated_fst_c.h"
//#include <unistd.h>
//#include "opcode/snowhousecpu-dasm-info-funcs.h"

//struct snowhousecpu_dasm_info_t;
//struct snowhousecpu_dasm_info_t;
//typedef int (*snowhousecpu_dasm_info_rd32_func)
//  (struct snowhousecpu_dasm_info_t * /* self */);
//
//extern "C" {
//extern void snowhousecpu_dasm_info_ctor (
//    snowhousecpu_dasm_info_t* self,
//);
//}

using namespace liborangepower::misc_output;
using namespace liborangepower::integer_types;

using snowhousecpu_dasm_info_rd32_func = int (*)(
    //struct snowhousecpu_dasm_info_t * /* self */
    u8* buf, size_t offset
);

extern "C" {
//--------
extern int snprint_one_insn_snowhousecpu(
    u32* curr_pc,
    char* str_buf, size_t str_buf_size,
    snowhousecpu_dasm_info_rd32_func rd32_func,
    u32* just_check_for_pre,
    bool show_enc_instr
);
//--------
}
static int my_rd32_func(u8* buf, size_t offset);

//static std::array<u8, sizeof(u32)> rd32_buf;
class SnowhousecpuDasm final {
private:     // variables
    //u32 rd32_buf_src = u32(0x0u);
    u32 _curr_pc = 0u;
    bool _have_pre: 1 = false;
    u32 _pre_imm = 0x0u;
    std::array<u32, 2ul> _enc_instr_buf = {u32(0x0u), u32(0x0u)};
    std::array<char, 128u> _dasm_buf;
public:     // functions
    inline int my_rd32_func(u8* buf, size_t offset) {
        //memcpy(buf, &rd32_buf_src, sizeof(rd32_buf_src));
        //printout(
        //    "SnowhousecpuDasm::my_rd32_func(): debug: ",
        //    "offset:", offset,
        //    "\n"
        //);
        if (offset == 0) {
            memcpy(
                buf,
                //_enc_instr_buf.data() + (offset / sizeof(u32)),
                &_enc_instr_buf[offset / sizeof(u32)],
                sizeof(u32)
            );
            return 0;
        } else if (offset == 4) {
            memcpy(
                buf,
                //_enc_instr_buf.data() + (offset / sizeof(u32)),
                &_enc_instr_buf[offset / sizeof(u32)],
                sizeof(u32)
            );
            //_have_pre = false;
            return 0;
        } else {
            return 1;
        }
    }

    bool push_back(u32 my_reg_pc, u32 enc_instr);

    inline bool have_pre() const {
        return _have_pre;
    }
    inline char* dasm_str() {
        return _dasm_buf.data();
    }
    inline const char* dasm_str() const {
        return _dasm_buf.data();
    }
};

static SnowhousecpuDasm dasm;

bool SnowhousecpuDasm::push_back(
    u32 my_reg_pc, u32 enc_instr
) {
    _curr_pc = my_reg_pc;
    //_dasm_buf.fill('\0');
    const size_t buf_idx = (
        _have_pre
        ? size_t(1ul)
        : size_t(0ul)
    );
    _enc_instr_buf.at(buf_idx) = enc_instr;

    if (!_have_pre) {
        _dasm_buf.fill('\0');
        _have_pre = (
            snprint_one_insn_snowhousecpu(
                //&_curr_pc,
                nullptr,
                _dasm_buf.data(), _dasm_buf.size(),
                ::my_rd32_func,
                &_pre_imm,
                false
            )
            == 4 // indicates that we have pre
        );
        if (!_have_pre) {
            snprint_one_insn_snowhousecpu(
                &_curr_pc,
                _dasm_buf.data(), _dasm_buf.size(),
                ::my_rd32_func,
                nullptr,
                false
            );
            //printout(
            //    "debug (!_have_pre): dasm_str():",
            //    dasm_str(),
            //    "\n"
            //);
        }
        return !_have_pre;
    } else { // if (_have_pre)
        snprint_one_insn_snowhousecpu(
            &_curr_pc,
            _dasm_buf.data(), _dasm_buf.size(),
            ::my_rd32_func,
            nullptr,
            false
        );
        //printout(
        //    "debug (_have_pre): dasm_str():",
        //    dasm_str(),
        //    "\n"
        //);
        //return false;
        _have_pre = false;
        return true;
    }
}

//static u32 rd32_buf_src = u32(0x0u);
//std::array<u32, 2> enc_instr_buf;
static int my_rd32_func(u8* buf, size_t offset) {
    return dasm.my_rd32_func(buf, offset);
}

//using std::cout;
//using std::cin;
//using std::cerr;
namespace sdl = liborangepower::sdl;
using liborangepower::math::Vec2;
using namespace liborangepower::misc_output;

static constexpr double
    CLK_RATE
        //= 25.0,
        //= 50.0,
        //= 75.0,
        = 100.0,
        //= 125.0,
        //= 150.0,
        //= 200.0,
        //= 24.0,
    PIXEL_CLK
        //= 100.0;
        = 25.0;
        //= 12.5;
        //= 6.0;
static constexpr size_t
    CLKS_PER_PIXEL = size_t(CLK_RATE / PIXEL_CLK);
    //PIXELS_PER_CLK = PIXEL_CLK / CLK_RATE;
static constexpr Vec2<size_t>
    HALF_SIZE_2D{
        //.x=1 << 7,
        //.y=1 << 7,
        //.x=1 << 6,
        //.y=1 << 5,
        //.x=320,
        //.y=240,
        .x=640,
        .y=480,
        //.x=639,
        //.y=480,
        //.x=128,
        //.x=160,
        //.y=128,
    },
    SIZE_2D{.x=HALF_SIZE_2D.x << 1, .y=HALF_SIZE_2D.y << 1};
    //SIZE_2D=HALF_SIZE_2D;

class Display {
public:     // variables
    sdl::Window window;
    sdl::Renderer renderer;
    sdl::Texture texture;
    std::unique_ptr<Uint32[]> pixels;
    Vec2<Uint32> pos{.x=0, .y=0};
    Vec2<Uint32> prev_pos{.x=0, .y=0};
    //Vec2<double> pos{.x=0.0, .y=0.0};
    size_t cnt_x = 0;
public:     // functions
    inline Display(bool have_window=true)
        : window(
            have_window
            ? SDL_CreateWindow(
                "VGA",                  // title
                SDL_WINDOWPOS_CENTERED, // x
                SDL_WINDOWPOS_CENTERED, // y
                SIZE_2D.x,              // WIDTH
                SIZE_2D.y,              // HEIGHT
                //HALF_SIZE_2D.x,               // WIDTH
                //HALF_SIZE_2D.y,               // HEIGHT
                                        // flags
                (
                    SDL_WINDOW_SHOWN
                    //| SDL_WINDOW_RESIZABLE
                )
            )
            : nullptr
        ),
        renderer(
            have_window
            ? SDL_CreateRenderer(
                window, // window
                -1,     // index
                0       // flags
            )
            : nullptr
        ),
        texture(
            have_window
            ? SDL_CreateTexture(
                renderer,
                SDL_PIXELFORMAT_ARGB8888,
                SDL_TEXTUREACCESS_STATIC,
                SIZE_2D.x,
                SIZE_2D.y
                //HALF_SIZE_2D.x,
                //HALF_SIZE_2D.y
            )
            : nullptr
        ),
        pixels(
            have_window
            ? new Uint32[SIZE_2D.x * SIZE_2D.y]
            : nullptr
        ),
        //pixels(new Uint32[HALF_SIZE_2D.x * HALF_SIZE_2D.y]),
        pos{.x=0, .y=0}
    {
        //SDL_SetWindowResizable(window, SDL_TRUE);
        //SDL_RenderSetScale(renderer, 2, 2);
        //SDL_RenderSetIntegerScale(renderer, SDL_TRUE);
        //SDL_RenderSetViewport(
        //  renderer,
        //  sdl::Rect(
        //      0, 0, // x, y
        //      WIDTH * 2, HEIGHT * 2
        //  )
        //);
        //SDL_RenderSetLogicalSize(renderer, SIZE_2D.x * 2, SIZE_2D.y * 2);
        if (pixels.get() != nullptr) {
            std::memset(
                pixels.get(),
                0,
                SIZE_2D.x * SIZE_2D.y * sizeof(Uint32)
                //HALF_SIZE_2D.x * HALF_SIZE_2D.y * sizeof(Uint32)
            );
        }
    }
    inline void set(
        Uint32 col
        //Uint32 col_r,
        //Uint32 col_g,
        //Uint32 col_b
    ) {
        if (pixels.get() == nullptr) {
            return;
        }
        //pixels.get()[pos.y * SIZE_2D.x + pos.x] = col;
        //if (
        //  //((col >> 20) & 0xf) == 0xf
        //  //&& ((col >> 12) & 0xf) == 0x8
        //  //&& ((col >> 4) & 0xf) == 0x0
        //  col_r == 0xf
        //  && col_g == 0x8
        //  && col_b == 0x0
        //) {
        //  printout(
        //      "Found orange: ",
        //      pos, "; ",
        //      std::hex,
        //          col, "; ",
        //          "{", col_r, " ", col_g, " ", col_b, "}",
        //      std::dec,
        //      "\n"
        //  );
        //}
        //pixels.get()[
        //  u32(pos.y) * HALF_SIZE_2D.x
        //  + u32(pos.x)
        //] = col;
        //pixels.get()[
        //  u32(pos.y) * HALF_SIZE_2D.x + u32(pos.x)
        //] = col;
        //pixels.get()[
        //  u32(pos.y) * SIZE_2D.x + u32(pos.x)
        //] = col;
        //printf(
        //  "set(%x): pos:(%u, %u)\n",
        //  col,
        //  pos.x, pos.y
        //);
        for (size_t j=0; j<2; ++j) {
            for (size_t i=0; i<2; ++i) {
                Vec2<Uint32> temp_pos;
                temp_pos.x = pos.x * 2 + i;
                temp_pos.y = pos.y * 2 + j;
                const Uint32 temp_idx = (
                    temp_pos.y * SIZE_2D.x + temp_pos.x
                );
                //if (j == 0 && i == 0) {
                //  printf(
                //      "set(%x) inner loop: (%lu, %lu) -> (%u, %u)\n",
                //      col,
                //      i, j,
                //      temp_pos.x, temp_pos.y
                //  );
                //}
                pixels.get()[temp_idx] = col;
            }
        }
    };
    inline void inc_x() {
        if (pixels.get() == nullptr) {
            return;
        }
        //++pos.x;
        //pos.x += PIXELS_PER_CLK;
        //if (pos.x >= HALF_SIZE_2D.x) {
        //  pos.x = HALF_SIZE_2D.x;
        //}

        //++cnt_x;
        //if ((cnt_x % CLKS_PER_PIXEL) == 0) {
        //  ++pos.x;
        //}
        //if (pos.x >= HALF_SIZE_2D.x) {
        //  cnt_x = HALF_SIZE_2D.x * CLKS_PER_PIXEL;
        //  pos.x = HALF_SIZE_2D.x;
        //}
        //--------
        //if (pos.x >= HALF_SIZE_2D.x) {
        //  //cnt_x = 0;
        //  pos.x = HALF_SIZE_2D.x;
        //} else {
        //  ++cnt_x;
        //  if (cnt_x >= CLKS_PER_PIXEL) {
        //      ++pos.x;
        //      cnt_x = 0;
        //  }
        //}
        //--------
        prev_pos.x = pos.x;
        ++pos.x;
        if (pos.x >= HALF_SIZE_2D.x) {
            pos.x = HALF_SIZE_2D.x;
        }
        //--------
        //if (cnt_x >= (HALF_SIZE_2D.x * CLKS_PER_PIXEL)) {
        //  cnt_x = HALF_SIZE_2D.x * CLKS_PER_PIXEL;
        //  pos.x = HALF_SIZE_2D.x;
        //}
        //--------
    };
    inline void inc_y() {
        if (pixels.get() == nullptr) {
            return;
        }
        prev_pos.y = pos.y;
        ++pos.y;
        if (pos.y >= HALF_SIZE_2D.y) {
            pos.y = HALF_SIZE_2D.y;
        }
    };

    inline void refresh() {
        if (pixels.get() == nullptr) {
            return;
        }
        //--------
        SDL_UpdateTexture(
            texture,
            NULL,
            pixels.get(),
            sizeof(Uint32) * SIZE_2D.x // pitch
            //sizeof(Uint32) * HALF_SIZE_2D.x // pitch
            //sizeof(Uint32) * SIZE_2D.x * SIZE_2D.y
        );
        SDL_RenderClear(renderer);
        SDL_RenderCopy(renderer, texture, NULL, NULL);
        SDL_RenderPresent(renderer);
        //std::memset(pixels.get(), 0, HALF_SIZE_2D.x * sizeof(Uint32));
        std::memset(
            pixels.get(), 0,
            SIZE_2D.x * SIZE_2D.y * sizeof(Uint32)
            //HALF_SIZE_2D.x * HALF_SIZE_2D.y * sizeof(Uint32)
        );
        //--------
        //--------
    };
    virtual void post_cycle() {
    }
    virtual void pre_cycle() {
    }
};

enum class SnesKeyKind: u32 {
    B = 0,
    Y = 1,
    Select = 2,
    Start = 3,
    DpadUp = 4,
    DpadDown = 5,
    DpadLeft = 6,
    DpadRight = 7,
    A = 8,
    X = 9,
    L = 10,
    R = 11,
    ExitSim = 12,
    Lim = 13,
};

class Vga: public Display{
protected:  // variables
    //Vga(VBriey* top,int SIZE_2D.x, int SIZE_2D.y) : Display() {
    //  this->top = top;
    //}
    //std::unique_ptr<VMeltedMoon> _top;
    //std::unique_ptr<VerilatedContext> _contextp;
    //VMeltedMoon* _top = nullptr;
    VMeltedMoonSimDut* _top = nullptr;
    sdl::KeyStatusUmap _key_status_umap;
    liborangepower::game::EngineKeyStatus _engine_key_status;
    u32
        _last_vsync = 0,
        _last_hsync = 0,
        _refresh_cnt = 0;
    enum class SnesKeyState: u32 {
        DriveValid,
        WaitFire,
    };
    SnesKeyState _snes_key_state = SnesKeyState::DriveValid;
    bool _do_exit = false;
protected:      // functions
    void _update_engine_key_status() {
        _engine_key_status.update(
            _key_status_umap,
            sdl::EngineKeycUmap<SnesKeyKind>({
                {SnesKeyKind::B, SDLK_k},
                {SnesKeyKind::Y, SDLK_j},
                {SnesKeyKind::Select, SDLK_a},
                {SnesKeyKind::Start, SDLK_RETURN},
                {SnesKeyKind::DpadUp, SDLK_e},
                {SnesKeyKind::DpadDown, SDLK_d},
                {SnesKeyKind::DpadLeft, SDLK_s},
                {SnesKeyKind::DpadRight, SDLK_f},
                {SnesKeyKind::A, SDLK_l},
                {SnesKeyKind::X, SDLK_i},
                {SnesKeyKind::L, SDLK_o},
                {SnesKeyKind::R, SDLK_p},
                {SnesKeyKind::ExitSim, SDLK_ESCAPE},
            })
        );
        //switch (_snes_key_state) {
        //  enum class Always: u32 {
        //      Disabled,
        //      KeyUp,
        //      KeyDown,
        //  };
        //  case SnesKeyState::DriveValid: {
        //      auto my_key_up_now
        //      = [&](
        //          const SnesKeyKind& key,
        //          Always always=Always::Disabled
        //      ) -> u32 {
        //          u32 my_key_status = 0b0u;
        //          switch (always) {
        //              case Always::Disabled:
        //                  my_key_status = u32(
        //                      _engine_key_status.key_up_now(key)
        //                  );
        //                  break;
        //              case Always::KeyUp:
        //                  my_key_status = 0b1u;
        //                  break;
        //              case Always::KeyDown:
        //                  my_key_status = 0b0u;
        //                  break;
        //          }
        //          return (
        //              my_key_status << u32(key)
        //          );
        //      };
        //      _top->io_rawSnesButtons_valid = true;
        //      //_top->io_rawSnesButtons_payload(0) = 3;
        //      _top->io_rawSnesButtons_payload = (
        //          my_key_up_now(
        //              SnesKeyKind::B//,
        //              //Always::KeyDown
        //              ////Always::Disabled
        //          )
        //          | my_key_up_now(
        //              SnesKeyKind::Y//,
        //              //Always::Disabled
        //          )
        //          | my_key_up_now(SnesKeyKind::Select)
        //          | my_key_up_now(SnesKeyKind::Start)
        //          | my_key_up_now(SnesKeyKind::DpadUp)
        //          | my_key_up_now(SnesKeyKind::DpadDown)
        //          | my_key_up_now(SnesKeyKind::DpadLeft)
        //          | my_key_up_now(SnesKeyKind::DpadRight)
        //          | my_key_up_now(
        //              SnesKeyKind::A//,
        //              //Always::KeyDown
        //              ////Always::Disabled
        //          )
        //          | my_key_up_now(SnesKeyKind::X)
        //          | my_key_up_now(SnesKeyKind::L)
        //          | my_key_up_now(
        //              SnesKeyKind::R//,
        //              //Always::KeyDown
        //          )
        //          | 0xf000
        //      );
        //      if (_engine_key_status.key_down_now(SnesKeyKind::ExitSim)) {
        //          _do_exit = true;
        //          printf("Exiting...\n");
        //      }
        //      //printf("0x%x\n", u32(_top->io_rawSnesButtons_payload));
        //      _snes_key_state = SnesKeyState::WaitFire;
        //  }
        //      break;
        //  case SnesKeyState::WaitFire: {
        //      if (
        //          //_top->io_rawSnesButtons_valid
        //          //&& 
        //          _top->io_rawSnesButtons_ready
        //      ) {
        //          //printf("testificate\n");
        //          //printf("_top->io_rawSnesButtons_ready == true\n");
        //          _top->io_rawSnesButtons_valid = false;
        //          _snes_key_state = SnesKeyState::DriveValid;
        //      }
        //  }
        //      break;
        //  default:
        //      break;
        //}
    }
    void _handle_sdl_events() {
        bool ksm_perf_total_backup = true;
        SDL_Event e;

        while (SDL_PollEvent(&e) != 0) {
            if (e.type == SDL_QUIT) {
                _do_exit = true;
                printf("Exiting...\n");
            } else if (
                liborangepower::sdl::handle_key_events(
                    e,
                    _key_status_umap, 
                    ksm_perf_total_backup
                )
            ) {
            }
        }
        _update_engine_key_status();
    }
public:     // functions
    inline Vga(VMeltedMoonSimDut* s_top, bool have_window=true)
        : Display(have_window=have_window),
        _top(s_top),
        //_top(new VMeltedMoonSimDut())
        //_contextp(new VerilatedContext()) 
        _engine_key_status(i32(SnesKeyKind::Lim))
        {
        //--------
        //_contextp->commandArgs(argc, argv);
        //--------
    }

    virtual ~Vga() {
    }

    virtual void post_cycle() {
        _handle_draw();
    }
    virtual void pre_cycle() {
        _handle_sdl_events();
        _handle_pos_update();
        _last_vsync = _top->vgaPhys_vsync;
        _last_hsync = _top->vgaPhys_hsync;
    }
    GEN_GETTER_BY_CON_REF(refresh_cnt);
    GEN_GETTER_BY_CON_REF(do_exit);
    GEN_SETTER_BY_VAL(do_exit);
protected:      // variables and helper functions
    //u32 _misc_visib_timer = 0;
    //bool _visib_enable = false;
    //bool _last_visib = false;
    //Vec2<Uint32> _temp_cnt{0, 0};
    bool _did_first_refresh = false;

    //Vec2<Uint32> _prev_pos = {0, 0}; //= pos;
    void _handle_pos_update() {
        if (pixels.get() == nullptr) {
            return;
        }

        const bool old_did_first_refresh = _did_first_refresh;
        //const Uint32 prev_pos_x = pos.x;
        //const Vec2<Uint32> prev_pos = pos;
        //_prev_pos = pos;
        if (
            _top->vgaPhys_vsync
            && !_last_vsync
            //!_top->vgaPhys_vsync
            //&& _last_vsync
            && (
                pos.y >= HALF_SIZE_2D.y
                || !_did_first_refresh
            )
        ) {
            if (!_did_first_refresh) {
                _did_first_refresh = true;
            }
            ++_refresh_cnt;
            //printf(
            //    "refreshing: x, y: %u, %u\n",
            //    pos.x, pos.y
            //);
            pos.y = 0;
            refresh();
        } 
        //else
        if (old_did_first_refresh) {
            if (
                _top->vgaPhys_hsync
                && !_last_hsync
                //!_top->vgaPhys_hsync
                //&& _last_hsync
                && pos.x != 0
                //&& pos.x >= HALF_SIZE_2D.x
            ) {
                inc_y();
                cnt_x = 0;
                pos.x = 0;
            }
            if (
                //_visib_enable
                //_top->io_misc_pastVisib
                //_top->io_misc_visib
                //_top->io_misc_visibPrev1
                //&& 
                _top->vgaVisib
                //--------
                && _top->vgaPixelEn
                //&& _top->io_misc_pastPixelEn
                //--------
            ) {
                //printf("testificate\n");
                inc_x();
            }
        }
        //if (
        //  pos.y != _prev_pos.y
        //) {
        //  printf(
        //      "pos.y changed: x, y: %u, %u\n",
        //      pos.x, pos.y
        //  );
        //}
    }
    void _handle_draw() {
        if (pixels.get() == nullptr) {
            return;
        }
        if (
            _top->vgaVisib
            && _top->vgaPixelEn
        ) {
            if (
                pos.x < HALF_SIZE_2D.x
                && pos.y < HALF_SIZE_2D.y
            ) {
                this->set(
                    (
                        //((_top->vgaPhys_col_r & 0xf) << 20)
                        //+ ((_top->vgaPhys_col_g & 0xf) << 12)
                        //+ ((_top->vgaPhys_col_b & 0xf) << 4)
                        //((_top->vgaPhys_col_r & 0xff) << 16)
                        //+ ((_top->vgaPhys_col_g & 0xff) << 8)
                        //+ ((_top->vgaPhys_col_b & 0xff) << 0)
                        ((_top->vgaPhys_col_r & 0xff) << (16))
                        + ((_top->vgaPhys_col_g & 0xff) << (8))
                        + ((_top->vgaPhys_col_b & 0xff) << (0))
                        //((_top->vgaPhys_col_r & 0xff) << (16 + 5))
                        //+ ((_top->vgaPhys_col_g & 0xff) << (8 + 5))
                        //+ ((_top->vgaPhys_col_b & 0xff) << (0 + 5))
                        //((_top->vgaPhys_col_b & 0x1f) << (16 + 3))
                        //+ ((_top->vgaPhys_col_g & 0x1f) << (8 + 3))
                        //+ ((_top->vgaPhys_col_r & 0x1f) << (0 + 3))
                    )
                    //_top->vgaPhys_col_r & 0xf,
                    //_top->vgaPhys_col_g & 0xf,
                    //_top->vgaPhys_col_b & 0xf
                );
            }
        } 
    }
public:     // functions
};

#ifndef TRACE_INST
#define TRACE_INST nullptr//new VerilatedFstC
#endif
#define HAVE_GRAPHICS

int main(int argc, char** argv) {
    if (SDL_Init(SDL_INIT_VIDEO) < 0) {
        return 1;
    }
    //std::unique_ptr<SDL_Window> win(new SDL_Window);
    //SDL_Window* win = nullptr;
    std::unique_ptr<
        VerilatedFstC
        //VerilatedVcdC
    > trace(
        //nullptr 
        //new VerilatedFstC
        TRACE_INST
    );
    if (trace) {
        Verilated::traceEverOn(true);
    }
    std::unique_ptr<VMeltedMoonSimDut> top(
        new VMeltedMoonSimDut()
    );
    std::unique_ptr<VerilatedContext> contextp(new VerilatedContext());
    //if (trace) {
    //}
    Vga vga(
        top.get(),
        #ifdef HAVE_GRAPHICS
        true
        #else
        false
        #endif
    );
    contextp->randReset(2);
    if (trace) {
        contextp->traceEverOn(true);
    }
    contextp->commandArgs(argc, argv);

    auto do_open_trace = [&](
        int levels=(
            //4
            20
        ),
        const char* fname="MeltedMoonDebug-sdl_test.fst"
    ) -> void {
        assert(bool(trace));
        if (trace) {
            assert(!trace->isOpen());
            top->trace(
                trace.get(),
                //20
                //4
                levels
            );
            trace->open(fname);
        }
    };

    //if (trace) {
    //    top->trace(
    //        trace.get(),
    //        //20
    //        4
    //    );
    //    trace->open("MeltedMoonDebug-sdl_test.fst");
    //}

    top->clk = 0;
    top->reset = 1;
    //top->vgaClk_clk = 0;
    //top->vgaClk_reset = 1;

    size_t tick_cnt = 0;
    auto end_tick = [&]() -> void {
        ++tick_cnt;
        top->eval();
        if (
            trace
            && trace->isOpen()
        ) {
            trace->dump(10 * tick_cnt - 1);
            if (!top->clk) {
                trace->flush();
            }
        }
    };
    auto set_clks_from_tick_cnt = [&]() -> void {
        //top->clk = tick_cnt % 2;
        //top->vgaClk_clk = tick_cnt % (2 * CLKS_PER_PIXEL);
        top->clk = !top->clk;
        //if ((tick_cnt % (2 * CLKS_PER_PIXEL)) == 0) {
        //  top->vgaClk_clk = !top->vgaClk_clk;
        //}
    };

    while (
        top->reset == !0
        //|| top->vgaClk_reset == !0
    ) {
        contextp->timeInc(1);
        //top->clk = !top->clk;

        //top->clk = tick_cnt % 2;
        //top->vgaClk_clk = tick_cnt % (2 * CLKS_PER_PIXEL);
        set_clks_from_tick_cnt();

        if (!top->clk) {
            if (
                contextp->time() > 1
                && contextp->time() < 10 * CLKS_PER_PIXEL
            ) {
                top->reset = 1;
            } else {
                top->reset = 0;
            }
        }
        //if (!top->vgaClk_clk) {
        //  //if (
        //  //  contextp->time() > 1 && contextp->time() < 10
        //  //) {
        //  //  top->reset = 1;
        //  //} else {
        //  //  top->reset = 0;
        //  //}
        //  if (
        //      contextp->time() > 1
        //      && contextp->time() < 10  * CLKS_PER_PIXEL
        //  ) {
        //      top->vgaClk_reset = 1;
        //  } else {
        //      top->vgaClk_reset = 0;
        //  }
        //}
        //top->eval();
        //trace->dump(1);
        //if (!top->clk) {
        //  trace->flush();
        //}
        end_tick();
    }

    //bool prev_clk = false;
    //for (;;) 

    std::string to_dbg_print = "";

    //rd32_buf.fill(0x0u);

    std::array<u32, 16u> saved_gprs_arr;
    saved_gprs_arr.fill(0x0u);
    u32 saved_reg_pc = 0x0u;
    //std::ofstream ofile("meltedMoonDebugSim-output.s");
    std::ofstream ofile;

    //auto& ofile = std::cout;
    //size_t prev_should_ignore_instr = false;
    //bool prev_other_temp_cond = false;
    auto should_write_ofile = [&]() -> bool {
        return (
            (
                !trace
                || trace->isOpen()
            )
            &&
            ofile.is_open()
        );
    };

    for 
    //while
    (
        ////!vga.do_exit()
        ///*;*/size_t i=0
        ;
        ////i<(HALF_SIZE_2D.x * HALF_SIZE_2D.y * 2 * CLKS_PER_PIXEL * 1.5)
        //////i<(HALF_SIZE_2D.x * HALF_SIZE_2D.y * 40 * 2) && !vga.do_exit();
        //////i<(HALF_SIZE_2D.x * HALF_SIZE_2D.y * 4 * 2) && !vga.do_exit()
            //i<(HALF_SIZE_2D.x * HALF_SIZE_2D.y /* * CLKS_PER_PIXEL */ * 5 * 2)
            //&& 
            !vga.do_exit()
        ;
        //////!vga.do_exit();
        //++i
    ) {
        //if (
        //    !ofile.is_open()
        //    //&& (
        //    //    to_dbg_print
        //    //    == (
        //    //        "ST_Init: Init status bar."
        //    //    )
        //    //)
        //) {
        //    ofile.open("meltedMoonDebugSim-output.s");
        //}
        contextp->timeInc(1);
        //top->clk = !top->clk;
        //top->clk = tick_cnt % 2;
        set_clks_from_tick_cnt();

        //if (
        //  prev_clk != top->vgaClk_clk
        //  //top->vgaClk_clk
        //  //!top->clk
        //) {
            if (
                !top->clk
                //!top->vgaClk_clk
            ) {
                vga.pre_cycle();
            } else {
                vga.post_cycle();
            }
        //}
        //printf(
        //  //"testificate: %x %x; %lu; %lu %lu\n",
        //  "testificate: %x %x; %lu\n",
        //  u32(top->clk),
        //  u32(top->vgaClk_clk),
        //  tick_cnt
        //  //(tick_cnt % 2) == 0,
        //  //(tick_cnt % (2 * CLKS_PER_PIXEL)) == 0
        //);
        //if (myOutpChar.valid.toBoolean) {
        //  if (myOutpChar.payload.toInt.toChar != '\n') {
        //    toDbgPrint = toDbgPrint + myOutpChar.payload.toInt.toChar
        //  } else {
        //    println(toDbgPrint)
        //    if (pw != null) {
        //      pw.write(toDbgPrint + s"\n")
        //      //if (
        //      //  toDbgPrint
        //      //  == "Error: R_InitTextures: Missing patch in texture COMP2"
        //      //) {
        //      //  pw.close()
        //      //  i = numClkCycles.toLong
        //      //}
        //    }
        //    toDbgPrint = ""
        //  }
        //}

        //if (
        //  pw != null
        //  && myRegFileWriteActive
        //) {
        //  val tempCond = Array[Boolean](
        //    myRegFileWriteData != mySavedRegsArr(myRegFileWriteAddr),
        //    mySavedRegsArr.last != myLaggingRegPcAtRegFileWrite,
        //  )
        //  if (
        //    tempCond(0)
        //    || tempCond(1)
        //  ) {
        //    val myPcChngStr = (
        //      if (tempCond(1)) (
        //        "(y chng)"
        //      ) else (
        //        "(n chng)"
        //      )
        //    )
        //    pw.write(
        //      s"pc${myPcChngStr}:"
        //        + f"$myLaggingRegPcAtRegFileWrite%08x" + s"    "
        //      + s"wrAddr:" + f"$myRegFileWriteAddr" + s"    "
        //      + s"wrData:" + f"$myRegFileWriteData%08x" + s"    "//s"\n"
        //      //+ s"tempCond:(${tempCond(0)} ${tempCond(1)})" + s"\n"
        //      //+ s"\n"
        //    )
        //    pw.write(
        //      s"gprs:("
        //    )
        //    for (idx <- 0 until mySavedRegsArr.size - 1) {
        //      if (idx < 13) {
        //        pw.write(s"r${idx}")
        //      } else if (idx == 13) {
        //        pw.write(s"lr")
        //      } else if (idx == 14) {
        //        pw.write(s"fp")
        //      } else if (idx == 15) {
        //        pw.write(s"sp")
        //      } else {
        //        pw.write(s"eek! ${idx}")
        //      }

        //      pw.write(
        //        s"="
        //        + f"${mySavedRegsArr(idx)}%08x"
        //      )
        //      if (idx + 1 < mySavedRegsArr.size - 1) {
        //        pw.write(
        //          s" "
        //        )
        //      }
        //    }
        //    pw.write(
        //      s")"
        //      + s"\n"
        //    )
        //    //if (tempCond(0)) {
        //      mySavedRegsArr(myRegFileWriteAddr) = myRegFileWriteData
        //    //}

        //    mySavedRegsArr(16) = myLaggingRegPcAtRegFileWrite
        //  }
        //}
        static constexpr const char* GPR_NAMES_ARR[16] = {
            "r0", "r1", "r2", "r3",
            "r4", "r5", "r6", "r7",
            "r8", "r9", "r10", "r11",
            "r12", "lr", "fp", "sp",
        };

        //const auto& my_wr_addr = top->cpuDbgInfo_regFileWriteAddr;
        const size_t my_wr_addr = (
            size_t(top->cpuDbgInfo_regFileWriteAddr)
        );
        const size_t my_wr_data = (
            size_t(top->cpuDbgInfo_regFileWriteData)
        );
        const std::array<u32, 3> my_rd_mem_word_arr = {
            u32(top->cpuDbgInfo_rdMemWordAtRegFileWrite_0),
            u32(top->cpuDbgInfo_rdMemWordAtRegFileWrite_1),
            u32(top->cpuDbgInfo_rdMemWordAtRegFileWrite_2),
        };
        const std::array<u32, 4> my_gpr_idx_arr = {
            u32(top->cpuDbgInfo_gprIdxVecAtRegFileWrite_0),
            u32(top->cpuDbgInfo_gprIdxVecAtRegFileWrite_1),
            u32(top->cpuDbgInfo_gprIdxVecAtRegFileWrite_2),
            u32(top->cpuDbgInfo_gprIdxVecAtRegFileWrite_3),
        };

        const size_t my_reg_pc = (
            top->cpuDbgInfo_laggingRegPcAtRegFileWrite
        );
        const size_t my_should_ignore_instr = (
            size_t(top->cpuDbgInfo_shouldIgnoreInstrAtRegFileWrite)
        );
        const u32 my_enc_instr = (
            u32(top->cpuDbgInfo_encInstrAtRegFileWrite)
        );
        const u32 my_imm = (
            u32(top->cpuDbgInfo_immAtRegFileWrite)
        );

        if (
            top->clk
            //!top->vgaClk_clk
        ) {
            const auto& temp_cond = std::array{
                (
                    bool(my_wr_data != saved_gprs_arr.at(my_wr_addr))
                    || (
                        my_wr_addr != 0x0
                        //&& (
                        //    my_gpr_idx_arr.at(0)
                        //    || my_gpr_idx_arr.at(1)
                        //    || my_gpr_idx_arr.at(2)
                        //    || my_gpr_idx_arr.at(3)
                        //)
                        //|| !my_should_ignore_instr
                    )
                ),
                bool(saved_reg_pc != my_reg_pc),
            };
            const bool my_full_temp_cond = (
                (
                    temp_cond.at(0)
                    || temp_cond.at(1)
                )
                && (
                    my_enc_instr != 0x0u
                )
            );
            //const bool other_temp_cond = (
            //    my_should_ignore_instr
            //    && !prev_should_ignore_instr
            //);
            //const bool other_temp_cond_pre = (
            //    dasm.have_pre()
            //);
            if (
                (
                    !my_should_ignore_instr
                    //|| other_temp_cond
                )
                && (
                    //temp_cond.at(0)
                    //|| temp_cond.at(1)
                    my_full_temp_cond
                )
            ) {
                dasm.push_back(
                    my_reg_pc,
                    my_enc_instr
                );
            }
            if (
                trace
                && !trace->isOpen()
                //&& vga.refresh_cnt() > 3
                //&& (my_reg_pc == u32(0x10u))
                //&& (my_reg_pc == u32(0x50c08u))
                && (
                    to_dbg_print
                    //== "inner: out of range (maybe?): {175, 103}"
                    == (
                        //"Rast::calc_visib(): _do_push_back(): "
                        //"y=96 x=136 iy=0 ix=136 "
                        //"N={0.000000, 0.000000, 1174.343750}"
                        //"R_InstallSpriteLump(): rotation == 0: sprtemp[frame].rotate:(0 0)"
                        //"P_InitPicAnims"
                        //"post S_UpdateSounds(...)"
                        "post D_Display()"
                    )
                )
                //&& (
                //    top->cpuDbgDbusWriteFire
                //)
            ) {
                printout(
                    "debug: opening the trace!\n"
                );
                do_open_trace(
                    //15
                    10
                    //5
                );
            }
            //if (
            //    trace
            //    && trace->isOpen()
            //    && (
            //        to_dbg_print.back() == '%'
            //    )
            //) {
            //    vga.set_do_exit(true);
            //}
            //if (my_reg_pc == u32(0x1c648u)) {
            //    printout(
            //        "debug: ",
            //        "have_pre:", dasm.have_pre(), " ",
            //        "shouldIgnoreInstr:",
            //            my_should_ignore_instr,
            //        "\n"
            //    );
            //}
            if (
                (
                    !my_should_ignore_instr
                    //&& (
                    //    //dasm.have_pre()
                    //    //|| 
                    //    top->regFileWriteActive
                    //)
                    && (
                        //temp_cond.at(0)
                        //|| temp_cond.at(1)
                        my_full_temp_cond
                    )
                )
                //|| (
                //    //my_should_ignore_instr
                //    //&& !prev_should_ignore_instr
                //    other_temp_cond
                //)
                //|| dasm.have_pre()
            ) {
                const std::string my_pc_chng_str = (
                    temp_cond.at(1)
                    ? "(y)"
                    : "(n)"
                );
                if (should_write_ofile()) {
                    fprintout(
                        ofile,
                        std::hex,
                        "pc", my_pc_chng_str, ":",
                        "0x", my_reg_pc, "    ",
                        std::dec
                    );
                    fprintout(
                        ofile,
                        "disasm:(",
                            dasm.dasm_str(),
                        ")",
                        "    "
                    );
                    fprintout(
                        ofile,
                        "("
                    );
                }
                for (size_t i=0; i<my_rd_mem_word_arr.size(); ++i) {
                    //if (
                    //    !temp_cond.at(1)
                    //    && (
                    //        my_rd_mem_word_arr.at(i)
                    //        != saved_gprs_arr.at(my_gpr_idx_arr.at(i))
                    //    )
                    //) {
                    //    fprintout(
                    //        ofile,
                    //        "eek! ",
                    //        "idx:", i, "    ",
                    //        std::hex,
                    //        "rdMemWord:", my_rd_mem_word_arr.at(i), "    ",
                    //        "saved:", 
                    //            saved_gprs_arr.at(my_gpr_idx_arr.at(i)),
                    //            "    ",
                    //        std::dec,
                    //        "gpr_idx:", my_gpr_idx_arr.at(i), "    ",
                    //        "\n\n"
                    //    );
                    //}
                    if (should_write_ofile()) {
                        fprintout(
                            ofile,
                                //"idx:", i, " ",
                                //"r", my_gpr_idx_arr.at(i), //" ",
                                GPR_NAMES_ARR[my_gpr_idx_arr.at(i)],
                                std::hex,
                                "=", "0x", my_rd_mem_word_arr.at(i)
                        );
                        if (i + 1 < my_rd_mem_word_arr.size()) {
                            fprintout(
                                ofile,
                                "  "
                                //";  "
                            );
                        }
                        fprintout(
                            ofile,
                            std::dec
                            //"\n"
                        );
                    }
                }
                if (should_write_ofile()) {
                    fprintout(
                        ofile,
                        ")",
                        "    "
                    );
                    fprintout(
                        ofile,
                        "wrAddr:", my_wr_addr, "    ",
                        std::hex,
                        "wrData:", "0x", my_wr_data, "    ",
                        "imm:", "0x", my_imm, "    ",
                        "dbusAddr:", "0x", 
                            (my_rd_mem_word_arr.at(
                                //my_wr_addr
                                //my_gpr_idx_arr.at(0)
                                0
                            ) + my_imm),
                        "    ",
                        std::dec
                    );
                    fprintout(
                        ofile,
                        "gprs:("
                    );
                }
                for (
                    size_t gpr_idx=0;
                    gpr_idx<saved_gprs_arr.size();
                    ++gpr_idx
                ) {
                    //if (gpr_idx < 13) {
                    //    fprintout(ofile, "r", gpr_idx);
                    //} else if (gpr_idx == 13) {
                    //    fprintout(ofile, "lr");
                    //} else if (gpr_idx == 14) {
                    //    fprintout(ofile, "fp");
                    //} else if (gpr_idx == 15) {
                    //    fprintout(ofile, "sp");
                    //} else {
                    //    fprintout(ofile, "eek! ");
                    //}
                    if (should_write_ofile()) {
                        fprintout(
                            ofile,
                            GPR_NAMES_ARR[gpr_idx],
                            "=",
                            std::hex,
                            "0x",
                            saved_gprs_arr.at(gpr_idx),
                            std::dec
                        );
                        if (gpr_idx + 1 < saved_gprs_arr.size()) {
                            fprintout(
                                ofile,
                                " "
                            );
                        }
                    }
                }
                if (should_write_ofile()) {
                    fprintout(
                        ofile,
                        ") "
                    );
                    fprintout(
                        ofile,
                        "encInstr:",
                        std::hex,
                        my_enc_instr,
                        std::dec,
                        " "
                    );
                    fprintout(
                        ofile,
                        "\n"
                    );
                }
                if (my_wr_addr != 0) {
                    saved_gprs_arr.at(my_wr_addr) = (
                        my_wr_data
                    );
                }
                saved_reg_pc = my_reg_pc;
            }

            if (top->outpChar_valid) {
                if (char(top->outpChar_payload) != '\n') {
                    to_dbg_print += char(top->outpChar_payload);
                } 
                //else if (char(top->outpChar_payload) > 'z') {
                //    //vga.set_do_exit(true);
                //}
                else {
                    if (should_write_ofile()) {
                        fprintout(
                            ofile,
                            to_dbg_print, "\n"
                        );
                    }
                    printout(to_dbg_print, "\n");
                    to_dbg_print = "";
                }
            }
        }

        //top->eval();
        //trace->dump(1);
        //if (!top->clk) {
        //  trace->flush();
        //}
        end_tick();
        //prev_clk = top->vgaClk_clk;
        //prev_should_ignore_instr = my_should_ignore_instr;
        //prev_other_temp_cond = other_temp_cond;
        //if (saved_reg_pc == 0x41f4ull) {
        //    break;
        //}

        //if (
        //    to_dbg_print
        //    //== "inner: out of range (maybe?): {143, 104}"
        //    //== "BaryLerp: A=-0.400695 B=-0.000000 C=1.400695"
        //    //== "BaryLerp: _inside_tri = true;"
        //    == "BaryLerp: returning!"
        //    //== (
        //    //    "BaryLerp: "
        //    //    "b_numer_det=-235.276718 "
        //    //    "c_numer_det=1610.628662 "
        //    //    "denom_det=1174.343750 "
        //    //    "one_over_denom_det=0.000852"
        //    //)
        //) {
        //    break;
        //}
    }
    if (
        //should_write_ofile()
        ofile.is_open()
    ) {
        ofile.close();
    }

    if (
        trace
        && trace->isOpen()
    ) {
        //if (!top->clk) {
        //    trace->flush();
        //}
        trace->close();
    }
    top->final();

    return 0;
}

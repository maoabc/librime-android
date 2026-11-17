package com.github.maoabc.librime;

import android.view.KeyEvent;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class RimeKeyMap {

    public static final int NONE = 0;
    public static final int SHIFT_MASK = 1;
    public static final int LOCK_MASK = 1 << 1;
    public static final int CONTROL_MASK = 1 << 2;
    public static final int MOD1_MASK = 1 << 3;
    public static final int ALT_MASK = MOD1_MASK;
    public static final int MOD2_MASK = 1 << 4;
    public static final int MOD3_MASK = 1 << 5;
    public static final int MOD4_MASK = 1 << 6;
    public static final int MOD5_MASK = 1 << 7;
    public static final int BUTTON1_MASK = 1 << 8;
    public static final int BUTTON2_MASK = 1 << 9;
    public static final int BUTTON3_MASK = 1 << 10;
    public static final int BUTTON4_MASK = 1 << 11;
    public static final int BUTTON5_MASK = 1 << 12;
    public static final int HANDLED_MASK = 1 << 24;
    public static final int FORWARD_MASK = 1 << 25;
    public static final int IGNORED_MASK = FORWARD_MASK;
    public static final int SUPER_MASK = 1 << 26;
    public static final int HYPER_MASK = 1 << 27;
    public static final int META_MASK = 1 << 28;
    public static final int RELEASE_MASK = 1 << 30;
    public static final int MODIFIER_MASK = 0x5f001fff;

    @IntDef(flag = true, value = {
            NONE,
            SHIFT_MASK,
            LOCK_MASK,
            CONTROL_MASK,
            MOD1_MASK,
            ALT_MASK,
            MOD2_MASK,
            MOD3_MASK,
            MOD4_MASK,
            MOD5_MASK,
            BUTTON1_MASK,
            BUTTON2_MASK,
            BUTTON3_MASK,
            BUTTON4_MASK,
            BUTTON5_MASK,
            HANDLED_MASK,
            FORWARD_MASK,
            IGNORED_MASK,
            SUPER_MASK,
            HYPER_MASK,
            META_MASK,
            RELEASE_MASK,
            MODIFIER_MASK
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface RimeModifier {
    }

    //@formatter:off

    public static final int XK_VoidSymbol                   = 0xffffff;  /* Void symbol */

    public static final int XK_BackSpace                    = 0xff08;  /* Back space, back char */
    public static final int XK_Tab                          = 0xff09;
    public static final int XK_Linefeed                     = 0xff0a;  /* Linefeed, LF */
    public static final int XK_Clear                        = 0xff0b;
    public static final int XK_Return                       = 0xff0d;  /* Return, enter */
    public static final int XK_Pause                        = 0xff13;  /* Pause, hold */
    public static final int XK_Scroll_Lock                  = 0xff14;
    public static final int XK_Sys_Req                      = 0xff15;
    public static final int XK_Escape                       = 0xff1b;
    public static final int XK_Delete                       = 0xffff;  /* Delete, rubout */



    /* Cursor control & motion */

    public static final int XK_Home                         = 0xff50;
    public static final int XK_Left                         = 0xff51;  /* Move left, left arrow */
    public static final int XK_Up                           = 0xff52;  /* Move up, up arrow */
    public static final int XK_Right                        = 0xff53;  /* Move right, right arrow */
    public static final int XK_Down                         = 0xff54;  /* Move down, down arrow */
    public static final int XK_Prior                        = 0xff55;  /* Prior, previous */
    public static final int XK_Page_Up                      = 0xff55;
    public static final int XK_Next                         = 0xff56;  /* Next */
    public static final int XK_Page_Down                    = 0xff56;
    public static final int XK_End                          = 0xff57;  /* EOL */
    public static final int XK_Begin                        = 0xff58;  /* BOL */



    /* Keypad functions, keypad numbers cleverly chosen to map to ASCII */

    public static final int XK_KP_Space                     = 0xff80;  /* Space */
    public static final int XK_KP_Tab                       = 0xff89;
    public static final int XK_KP_Enter                     = 0xff8d;  /* Enter */
    public static final int XK_KP_F1                        = 0xff91;  /* PF1, KP_A, ... */
    public static final int XK_KP_F2                        = 0xff92;
    public static final int XK_KP_F3                        = 0xff93;
    public static final int XK_KP_F4                        = 0xff94;
    public static final int XK_KP_Home                      = 0xff95;
    public static final int XK_KP_Left                      = 0xff96;
    public static final int XK_KP_Up                        = 0xff97;
    public static final int XK_KP_Right                     = 0xff98;
    public static final int XK_KP_Down                      = 0xff99;
    public static final int XK_KP_Prior                     = 0xff9a;
    public static final int XK_KP_Page_Up                   = 0xff9a;
    public static final int XK_KP_Next                      = 0xff9b;
    public static final int XK_KP_Page_Down                 = 0xff9b;
    public static final int XK_KP_End                       = 0xff9c;
    public static final int XK_KP_Begin                     = 0xff9d;
    public static final int XK_KP_Insert                    = 0xff9e;
    public static final int XK_KP_Delete                    = 0xff9f;
    public static final int XK_KP_Equal                     = 0xffbd;  /* Equals */
    public static final int XK_KP_Multiply                  = 0xffaa;
    public static final int XK_KP_Add                       = 0xffab;
    public static final int XK_KP_Separator                 = 0xffac;  /* Separator, often comma */
    public static final int XK_KP_Subtract                  = 0xffad;
    public static final int XK_KP_Decimal                   = 0xffae;
    public static final int XK_KP_Divide                    = 0xffaf;

    public static final int XK_KP_0                         = 0xffb0;
    public static final int XK_KP_1                         = 0xffb1;
    public static final int XK_KP_2                         = 0xffb2;
    public static final int XK_KP_3                         = 0xffb3;
    public static final int XK_KP_4                         = 0xffb4;
    public static final int XK_KP_5                         = 0xffb5;
    public static final int XK_KP_6                         = 0xffb6;
    public static final int XK_KP_7                         = 0xffb7;
    public static final int XK_KP_8                         = 0xffb8;
    public static final int XK_KP_9                         = 0xffb9;


            public static final int XK_F1                           = 0xffbe;
            public static final int XK_F2                           = 0xffbf;
            public static final int XK_F3                           = 0xffc0;
            public static final int XK_F4                           = 0xffc1;
            public static final int XK_F5                           = 0xffc2;
            public static final int XK_F6                           = 0xffc3;
            public static final int XK_F7                           = 0xffc4;
            public static final int XK_F8                           = 0xffc5;
            public static final int XK_F9                           = 0xffc6;
            public static final int XK_F10                          = 0xffc7;
            public static final int XK_F11                          = 0xffc8;
            public static final int XK_F12                          = 0xffc9;
            public static final int XK_F13                          = 0xffca;
            public static final int XK_F14                          = 0xffcb;
            public static final int XK_F15                          = 0xffcc;
            public static final int XK_F16                          = 0xffcd;
            public static final int XK_F17                          = 0xffce;
            public static final int XK_F18                          = 0xffcf;
            public static final int XK_F19                          = 0xffd0;
            public static final int XK_F20                          = 0xffd1;
            public static final int XK_F21                          = 0xffd2;
            public static final int XK_F22                          = 0xffd3;
            public static final int XK_F23                          = 0xffd4;
            public static final int XK_F24                          = 0xffd5;
            public static final int XK_F25                          = 0xffd6;
            public static final int XK_F26                          = 0xffd7;
            public static final int XK_F27                          = 0xffd8;
            public static final int XK_F28                          = 0xffd9;
            public static final int XK_F29                          = 0xffda;
            public static final int XK_F30                          = 0xffdb;
            public static final int XK_F31                          = 0xffdc;
            public static final int XK_F32                          = 0xffdd;
            public static final int XK_F33                          = 0xffde;
            public static final int XK_F34                          = 0xffdf;
            public static final int XK_F35                          = 0xffe0;



    /* Modifiers */

    public static final int XK_Shift_L                      = 0xffe1;  /* Left shift */
    public static final int XK_Shift_R                      = 0xffe2;  /* Right shift */
    public static final int XK_Control_L                    = 0xffe3;  /* Left control */
    public static final int XK_Control_R                    = 0xffe4;  /* Right control */
    public static final int XK_Caps_Lock                    = 0xffe5;  /* Caps lock */
    public static final int XK_Shift_Lock                   = 0xffe6;  /* Shift lock */

    public static final int XK_Meta_L                       = 0xffe7;  /* Left meta */
    public static final int XK_Meta_R                       = 0xffe8;  /* Right meta */
    public static final int XK_Alt_L                        = 0xffe9;  /* Left alt */
    public static final int XK_Alt_R                        = 0xffea;  /* Right alt */
    public static final int XK_Super_L                      = 0xffeb;  /* Left super */
    public static final int XK_Super_R                      = 0xffec;  /* Right super */
    public static final int XK_Hyper_L                      = 0xffed;  /* Left hyper */
    public static final int XK_Hyper_R                      = 0xffee;  /* Right hyper */


    //@formatter:on


    public static @RimeModifier int toRimeModifier(@NonNull KeyEvent event) {
        int modifier = 0;

        final int action = event.getAction();

        if (action == KeyEvent.ACTION_UP) modifier |= RELEASE_MASK;

        if (event.isCtrlPressed()) modifier |= CONTROL_MASK;
        if (event.isShiftPressed()) modifier |= SHIFT_MASK;
        if (event.isAltPressed()) modifier |= ALT_MASK;
        if (event.isCapsLockOn()) modifier |= LOCK_MASK;
        return modifier;
    }

    public static int toX11Keycode(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DEL:
                return XK_BackSpace;
            case KeyEvent.KEYCODE_TAB:
                return XK_Tab;
            case KeyEvent.KEYCODE_CLEAR:
                return XK_Clear;
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                return XK_Return;
            case KeyEvent.KEYCODE_BREAK:
                return XK_Pause;
            case KeyEvent.KEYCODE_SCROLL_LOCK:
                return XK_Scroll_Lock;
            case KeyEvent.KEYCODE_SYSRQ:
                return XK_Sys_Req;
            case KeyEvent.KEYCODE_ESCAPE:
                return XK_Escape;
            case KeyEvent.KEYCODE_FORWARD_DEL:
                return XK_Delete;

            case KeyEvent.KEYCODE_MOVE_HOME:
                return XK_Home;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                return XK_Left;
            case KeyEvent.KEYCODE_DPAD_UP:
                return XK_Up;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                return XK_Right;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                return XK_Down;
            case KeyEvent.KEYCODE_PAGE_UP:
                return XK_Page_Up;
            case KeyEvent.KEYCODE_PAGE_DOWN:
                return XK_Page_Down;
            case KeyEvent.KEYCODE_MOVE_END:
                return XK_End;

            case KeyEvent.KEYCODE_NUMPAD_ENTER:
                return XK_KP_Enter;
            case KeyEvent.KEYCODE_NUMPAD_EQUALS:
                return XK_KP_Equal;
            case KeyEvent.KEYCODE_NUMPAD_MULTIPLY:
                return XK_KP_Multiply;
            case KeyEvent.KEYCODE_NUMPAD_ADD:
                return XK_KP_Add;
            case KeyEvent.KEYCODE_NUMPAD_COMMA:
                return XK_KP_Separator;
            case KeyEvent.KEYCODE_NUMPAD_SUBTRACT:
                return XK_KP_Subtract;
            case KeyEvent.KEYCODE_NUMPAD_DOT:
                return XK_KP_Decimal;
            case KeyEvent.KEYCODE_NUMPAD_DIVIDE:
                return XK_KP_Divide;
            case KeyEvent.KEYCODE_NUMPAD_0:
                return XK_KP_0;
            case KeyEvent.KEYCODE_NUMPAD_1:
                return XK_KP_1;
            case KeyEvent.KEYCODE_NUMPAD_2:
                return XK_KP_2;
            case KeyEvent.KEYCODE_NUMPAD_3:
                return XK_KP_3;
            case KeyEvent.KEYCODE_NUMPAD_4:
                return XK_KP_4;
            case KeyEvent.KEYCODE_NUMPAD_5:
                return XK_KP_5;
            case KeyEvent.KEYCODE_NUMPAD_6:
                return XK_KP_6;
            case KeyEvent.KEYCODE_NUMPAD_7:
                return XK_KP_7;
            case KeyEvent.KEYCODE_NUMPAD_8:
                return XK_KP_8;
            case KeyEvent.KEYCODE_NUMPAD_9:
                return XK_KP_9;

            case KeyEvent.KEYCODE_F1:
                return XK_F1;
            case KeyEvent.KEYCODE_F2:
                return XK_F2;
            case KeyEvent.KEYCODE_F3:
                return XK_F3;
            case KeyEvent.KEYCODE_F4:
                return XK_F4;
            case KeyEvent.KEYCODE_F5:
                return XK_F5;
            case KeyEvent.KEYCODE_F6:
                return XK_F6;
            case KeyEvent.KEYCODE_F7:
                return XK_F7;
            case KeyEvent.KEYCODE_F8:
                return XK_F8;
            case KeyEvent.KEYCODE_F9:
                return XK_F9;
            case KeyEvent.KEYCODE_F10:
                return XK_F10;
            case KeyEvent.KEYCODE_F11:
                return XK_F11;
            case KeyEvent.KEYCODE_F12:
                return XK_F12;

            case KeyEvent.KEYCODE_SHIFT_LEFT:
                return XK_Shift_L;
            case KeyEvent.KEYCODE_SHIFT_RIGHT:
                return XK_Shift_R;
            case KeyEvent.KEYCODE_CTRL_LEFT:
                return XK_Control_L;
            case KeyEvent.KEYCODE_CTRL_RIGHT:
                return XK_Control_R;
            case KeyEvent.KEYCODE_CAPS_LOCK:
                return XK_Caps_Lock;
            case KeyEvent.KEYCODE_META_LEFT:
                return XK_Meta_L;
            case KeyEvent.KEYCODE_META_RIGHT:
                return XK_Meta_R;
            case KeyEvent.KEYCODE_ALT_LEFT:
                return XK_Alt_L;
            case KeyEvent.KEYCODE_ALT_RIGHT:
                return XK_Alt_R;

            default:
                return XK_VoidSymbol;
        }
    }

    public static int toX11Keycode(@NonNull KeyEvent event) {
        int x11Key = toX11Keycode(event.getKeyCode());
        if (x11Key != XK_VoidSymbol) {
            return x11Key;
        }
        int unicode = event.getUnicodeChar();
        return unicode != 0 ? unicode : XK_VoidSymbol;
    }

}

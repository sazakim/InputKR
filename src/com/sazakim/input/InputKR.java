package com.sazakim.input;

import processing.core.PApplet;
import processing.event.KeyEvent;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class InputKR {

	public final static String VERSION = "1.0.0";

	PApplet parent;

	public static char[] ja_han = {
			'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
	};
	public static char[] mo_han = {
			'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅛ', 'ㅜ', 'ㅠ', 'ㅡ', 'ㅣ'
	};

	public static char[] ja_eng = {
			'r', 'R', 's', 'e', 'E', 'f', 'a', 'q', 'Q', 't', 'T', 'd', 'w', 'W', 'c', 'z', 'x', 'v', 'g'
	};
	public static char[] mo_eng = {
			'k', 'o', 'i', 'O', 'j', 'p', 'u', 'P', 'h', 'y', 'n', 'b', 'm', 'l'
	};

	public static Set < Character > pressed_status;
	public static Map < Character, Character > hash;

	public String typingText = "";
	public String combinedText = "";

	public boolean isShiftPressed = false;
	public int lastShiftReleasedTime = 0;

	public boolean useGL = false;
	boolean hangul_input = false;
	boolean clearReady = false;
	boolean toggleReady = false;
	boolean clearOnEnter = true;

	Mode inputMode;

	enum Mode {
		WIN, WIN_GL, OSX, OSX_GL
	};

	enum Type {
		ASCII, HANGUL, JAUM, MOUM
	}

	static {
		hash = new HashMap();
		pressed_status = new HashSet();
		for( int i = 0 ; i < ja_han.length ; i++ )
			hash.put( ja_eng[ i ], ja_han[ i ] );
		for( int i = 0 ; i < mo_han.length ; i++ )
			hash.put( mo_eng[ i ], mo_han[ i ] );
	}

	public InputKR( PApplet parent ) {


		this.parent = parent;
		parent.registerMethod( "keyEvent", this );

		if ( parent.sketchRenderer() != PApplet.JAVA2D ) {
			useGL = true;
		}

		switch ( PApplet.platform ) {
		case PApplet.WINDOWS:
			inputMode = Mode.WIN;
			if ( useGL )
				inputMode = Mode.WIN_GL;
			break;
		case PApplet.MACOSX:
			inputMode = Mode.OSX;
			if ( useGL )
				inputMode = Mode.OSX_GL;
			break;
		default:
			inputMode = Mode.WIN;
			break;
		}

		try {
			parent.getClass().getMethod( "onTyping", String.class );
		} catch ( Exception e ) {
			parent.println( "Unable to find the function : ", "onTyping( String )" );
		}

		try {
			parent.getClass().getMethod( "onEnter", String.class );
		} catch ( Exception e ) {
			parent.println( "Unable to find the function : ", "onEnter( String )" );
		}

		setRepeat( true );
	}

	public void setClearOnEnter( boolean b ) {
		clearOnEnter = b;
	}

	public void setRepeat( boolean repeat ) {
		if ( repeat )
			parent.hint( parent.ENABLE_KEY_REPEAT );
	}

	private Type getType( char c ) {
		// parent.println( "getType: ", c, parent.parseInt(c), parent.parseInt('z'),
		// parent.hex(c), 'a' < c, c < 'Z' );
		if ( 0 <= c && c <= 127 ) {
			return Type.ASCII;
		} else if ( 'ㄱ' <= c && c <= 'ㅎ' ) { // 자음 { 0x3131~0x314e }
			return Type.JAUM;
		} else if ( 'ㅏ' <= c && c <= 'ㅣ' ) { // 모음 { 0x314f~0x3163 }
			return Type.MOUM;
		} else if ( 0xAC00 <= c && c <= 0xD7A3 ) { // 한글 글자 ( 전체 한글 11172 개 , 시작 글자(가) : 0xAC00 ~ 마지막 글자(힣) : 0xD7A3 )
			return Type.HANGUL;
		} else {
			return null;
		}
	}

	public void keyEvent( KeyEvent e ) {

		char key = e.getKey();
		int keyCode = e.getKeyCode();

		switch ( e.getAction() ) {

		case KeyEvent.PRESS:

			// parent.println( "press:", key, ":", keyCode, ":", parent.parseInt(key) );


			if ( clearOnEnter && ( keyCode == parent.ENTER || keyCode == parent.RETURN ) ) {
				onEnter();
			}


			if ( inputMode == Mode.WIN ) {
				if ( keyCode == parent.SHIFT ) {
					isShiftPressed = true;
					return;
				}
				pressed_status.add( key );
			} else if ( inputMode == Mode.WIN_GL ) {

				if ( isShiftPressed && keyCode == 32 ) {
					toggle();
					return;
				}

				if ( keyCode == 8 ) {
					delete();
				} else if ( keyCode == 147 ) {
					delete();
				} else {
					if ( hangul_input ) {
						if ( hash.containsKey( key ) ) {
							hangulPressed( hash.get( key ) );
						} else {
							normalPressed( key );
						}
					} else {
						normalPressed( key );
					}
				}

				if ( keyCode == parent.SHIFT )
					isShiftPressed = true;
			} else if ( inputMode == Mode.OSX || inputMode == Mode.OSX_GL ) {


				if ( getType( key ) == Type.JAUM || getType( key ) == Type.MOUM ) {
					hangulPressed( key );
				} else {
					if ( keyCode == 8 ) {
						delete();
					} else if ( keyCode == 147 ) {
						delete();
					} else {
						normalPressed( key );
					}
				}
			}

			break;
		case KeyEvent.RELEASE:

			// parent.println( "released:", key, ":", keyCode, ":", parent.parseInt(key) );

			if ( inputMode == Mode.WIN ) { // WIN_GL

				if ( keyCode == parent.SHIFT ) {
					lastShiftReleasedTime = parent.millis();
					isShiftPressed = false;
					break;
				}

				if ( parent.millis() - lastShiftReleasedTime < 200 )
					key = Character.toUpperCase( key );

				if ( getType( key ) == Type.ASCII && pressed_status.contains( key ) ) {
					pressed_status.remove( key );

					if ( keyCode == 8 ) {
						delete();
					} else if ( keyCode == 127 ) {
						delete();
					} else {
						normalReleased( key );
					}
				} else {
					if ( hash.containsKey( key ) ) {
						hangulReleased( hash.get( key ) );
					}
				}
			} else if ( inputMode == Mode.WIN_GL ) {
				if ( keyCode == parent.SHIFT )
					isShiftPressed = false;
			}

			if ( clearReady ) {
				typingText = "";
				combinedText = "";
				onTyping();
				clearReady = false;
			}

			if ( toggleReady ) {
				hangul_input = !hangul_input;
				toggleReady = false;
			}
			break;
		case KeyEvent.TYPE:
			break;
		}
	}

	public void toggle() {
		if ( inputMode != Mode.WIN_GL )
			parent.println( "toggle is only enabled in GL mode on Windows" );

		toggleReady = true;
	}

	private void onTyping() {
		try {
			parent.getClass().getMethod( "onTyping", String.class ).invoke( parent, combinedText );
		} catch ( Exception e ) {
			// parent.println("Unable to find the function : ", "onTyping( String )" );
		}
	}

	private void type( char c ) {
		if ( clearReady == false && toggleReady == false ) {
			typingText += c;
			combinedText = CombineKR.combine( typingText );
			onTyping();
		}
	}

	private void delete() {
		if ( typingText.length() > 0 ) {
			typingText = typingText.substring( 0, typingText.length() - 1 );
			combinedText = CombineKR.combine( typingText );
			onTyping();
		}
	}

	public void clear() {
		clearReady = true;
	}

	private void onEnter() {
		try {
			parent.getClass().getMethod( "onEnter", String.class ).invoke( parent, combinedText );
		} catch ( Exception e ) {
			// parent.println("Unable to find the function : ", "onEnter( String )" );
		}

		clear();
	}

	private void normalPressed( char c ) {
		if ( c != parent.CODED )
			type( c );
	}

	private void normalReleased( char c ) {
		if ( c != parent.CODED )
			type( c );
	}

	private void hangulPressed( char c ) {
		type( c );
	}

	private void hangulReleased( char c ) {
		type( c );
	}

}

// MAC_OSX: pressed, typed: autoRepeat (JAVA2D) | noRepeat (P3D / P2D)
// Windows10: pressed, typed: autoRepeat (JAVA2D) | noRepeat (P3D / P2D)

// MAC_OSX JAVA2D/P2D/P3D: pressed and released: unicode (자음, 모음 개별로)
// MAC_OSX_Catalina (JAVA2D) : CapsLock : pressed: keyCode20, released: keyCode20, pressed: keyCode0, char 65535
// MAC_OSX_Catalina (P2D/P3D) : CapsLock : no Event

//윈도우즈 한글 입력시: 첫글자 입력: 프레스 무시, 릴리즈만 영문 소문자로 / 두 번재 글자 입력: 타이프만 ㅁ, 프레스 없음, 릴리즈 영문 소문자 

// Windows10 JAVA2D/P2D/P3D:     a :: p(65:a:97)t(0:a:97)r(65:a:97)     A :: p(16:?:65535) + p(65:A:65)t(0:A:65)r(65:A:65) + r(16:?:65535)
// Windows10 P2D/P3D:     ㄱ :: p(82:r:114)t(0:r:114)r(82:r:114)     ㄲ :: p(16:?:65535) + p(82:R:82)t(0:R:82)r(82:R:82) + r(16:?:65535)
// Windows10 JAVA2D: BackSpace: 8::8   Delete: 147::127  Left-Alt:18   Right-Alt:19(only release)   Left-Win:157  Right-Win:157  Left-Ctrl: 17  Right-Ctrl:17 Left-Hanja: -  right(?):153,0
// Windows10 P2D/P3D: BackSpace: 8::8   Delete: 127::127  Left-Alt:18 Right-Alt:0   Left-Win:524  Right-Win:524  Left-Ctrl: 17  Right-Ctrl:263 Left-Hanja:263  right(?):525
// Windows10 (JAVA2D) : CapsLock : pressed: keyCode20, released: keyCode20, char 65535
// Windows10 (P2D/P3D) : CapsLock : pressed: keyCode20/char0, typed: keyCode0/char0, released: keyCode20/char0

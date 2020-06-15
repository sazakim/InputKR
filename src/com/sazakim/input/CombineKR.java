package com.sazakim.input;

public class CombineKR {

	public final static String VERSION = "1.0.0";

	public static String[] chosung_str = {
			"ㄱ", "ㄲ", "ㄴ", "ㄷ", "ㄸ", "ㄹ", "ㅁ", "ㅂ", "ㅃ", "ㅅ", "ㅆ", "ㅇ", "ㅈ", "ㅉ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ"
	};

	public static String[] jungsung_str = {
			"ㅏ", "ㅐ", "ㅑ", "ㅒ", "ㅓ", "ㅔ", "ㅕ", "ㅖ", "ㅗ", "ㅗㅏ", "ㅗㅐ", "ㅗㅣ", "ㅛ", "ㅜ", "ㅜㅓ", "ㅜㅔ", "ㅜㅣ", "ㅠ", "ㅡ", "ㅡㅣ", "ㅣ"
	};

	public static char[] jungsung_char = {
			'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ', 'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'
	};

	public static String[] jongsung_str = {
			"", "ㄱ", "ㄲ", "ㄱㅅ", "ㄴ", "ㄴㅈ", "ㄴㅎ", "ㄷ", "ㄹ", "ㄹㄱ", "ㄹㅁ", "ㄹㅂ", "ㄹㅅ", "ㄹㅌ", "ㄹㅍ", "ㄹㅎ", "ㅁ", "ㅂ", "ㅂㅅ", "ㅅ", "ㅆ", "ㅇ", "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ"
	};

	public static char[] jongsung_char = {
			'\0', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
	};

	private static int getCharIndex( String[] str_array, String str ) {
		for( int i = 0 ; i < str_array.length ; i++ ) {
			if ( str_array[ i ].equals( str ) )
				return i;
		}
		return -1;
	}

	private static char getHangulChar( int chosung_index, int jungsung_index, int jongsung_index ) {
		int base_code = '가' + chosung_index * 21 * 28; // 한글 시작 ('가') 코드 + 초성 순서( 0 ~ 18 ) * 모음수(21) * 종성수(28)
		int jungsung_code_add = jungsung_index * 28; // 중성 순서( 0 ~ 20 ) * 종성수(28)
		int jongsung_code_add = jongsung_index; // 종성 순서
		return ( char ) ( base_code + jungsung_code_add + jongsung_code_add );
	}

	private static String getSingle( String str, int i ) {
		if ( i < str.length() )
			return str.substring( i, i + 1 );
		return null;
	}

	private static String getDouble( String str, int i ) {
		if ( i + 1 < str.length() )
			return str.substring( i, i + 2 );
		return null;
	}

	public static String combine( String str ) {

		String result = ""; // 완성 한글을 담기 위한 문자열 변수
		int first_index = 0; // 첫글자 인덱스 위치

		while ( first_index < str.length() ) {

			int second_index = 0; // 두번째 위치 (일반적인 모음 위치)
			int third_index = 0; // 세번째 위치 (종성 위치)
			int fourth_index = 0; // 네번째 위치 (2글자 조합의 종성의 일부 혹은, 다음 글자의 첫글자 위치)

			String firstChar = getSingle( str, first_index ); // 한글자 따오기
			int cho_index = -1;
			int jung_index = -1;
			int jong_index = -1;

			if ( 'ㄱ' <= firstChar.charAt( 0 ) && firstChar.charAt( 0 ) <= 'ㅎ' ) { // 자음 { 0x3131~0x314e }
				// 초성 인덱스
				cho_index = getCharIndex( chosung_str, firstChar );

				second_index = first_index + 1;

				// 두 글자 조합의 모음 코드 체크
				int double_moum_code = getCharIndex( jungsung_str, getDouble( str, second_index ) );

				// 두 글자 모음 찾으면 [ ㅇ(ㅗㅣ)ㄹㅗㅇㅜㄴ ], 중성 코드 저장 후, 종성 체크로 이동.
				if ( double_moum_code != -1 ) {
					jung_index = double_moum_code;
					third_index = second_index + 2; // ( 두 글자 건너뛴 종성체크 위치 지정)
				} else {
					// 두 글자 모음이 없다면, 한 글자의 모음 코드 체크 : [ ㅎ(ㅏ)ㄴㄱㄹ ]
					int single_moum_code = getCharIndex( jungsung_str, getSingle( str, second_index ) );
					if ( single_moum_code != -1 ) {
						// 단일 모음 코드 있다면
						jung_index = single_moum_code;
						third_index = second_index + 1; // ( 한 글자 건너뛴 종성체크 위치 지정 )
					} else {
						// 단일 모음 코드 없는 경우

						if ( first_index + 1 < str.length() ) {
							// 이어지는 글자가 하나 더 있는 경우
							String doubleJongSung = getDouble( str, first_index );
							if ( getCharIndex( jongsung_str, doubleJongSung ) != -1 ) {
								// 두 글자의 조합이 복합 자음인 경우
								result += ( jongsung_char[ getCharIndex( jongsung_str, doubleJongSung ) ] ); // 복합 자음 한글자 저장
								first_index = first_index + 2; // 다음 첫글자 위치 지정
								continue;
							}
						}
						result += ( firstChar ); // 단일 자음 할당
						first_index = first_index + 1;
						continue;
					}
				}

				// 2자 조합의 종성코드 추출
				int double_jaum_code = getCharIndex( jongsung_str, getDouble( str, third_index ) );
				if ( double_jaum_code != -1 ) {
					// 2자 종성코드를 찾았으면, 일단 종성에 할당
					jong_index = double_jaum_code;

					// 2자 종성에 연이은 모음이 존재하는지 검사.
					fourth_index = third_index + 2;
					int next_moum_code = getCharIndex( jungsung_str, getSingle( str, fourth_index ) );

					// tempjung_index = getCharIndex( jungsung_str, getDouble(jamoStr, i + 2) );
					if ( next_moum_code != -1 ) {
						// 2자 종성이 검색 되더라도, 연이은 모음이 있을 경우[ ㄴㅏㄹㄱ(ㅐ) ], 한 개의 받침만 종성으로 변경 [ ㄴㅏㄹ / ㄱㅐ ]
						jong_index = getCharIndex( jongsung_str, getSingle( str, third_index ) );
						first_index = third_index + 1; // 다음 글자 시작 위치 지정
					} else {
						// 2자 종성이 검색 되고, 연이은 모음이 없으면 ( ㄷㅏㄹㄱ,ㄱㅗㄱㅣ ), 최종 글자 조합으로 진행
						first_index = third_index + 2; // 다음 글자 시작 위치 지정
					}
				} else {
					// 2자의 종성코드 못찾았을 경우, 그 다음의 중성 문자에 대한 코드 추출.
					fourth_index = third_index + 1;
					int next_moum_code = getCharIndex( jungsung_str, getSingle( str, fourth_index ) );

					if ( next_moum_code != -1 ) {
						// 종성 위치 바로 다음에 중성 문자가 존재하면, 받침이 없는 글자로 여기고( 인덱스 0 ), 종성 위치의 글자는 다음 글자의 초성으로 넘김
						jong_index = 0;
						first_index = third_index; // 다음 글자가 종성 위치에서 시작하도록
					} else {
						// 단일조합 종성코드 추출
						int singleJongsungCode = getCharIndex( jongsung_str, getSingle( str, third_index ) );

						if ( singleJongsungCode != -1 ) {
							// 2자 종성 검색은 안되었으나, 1자 종성은 존재하는 경우
							jong_index = singleJongsungCode;
							first_index = third_index + 1; // 다음 글자의 시작 위치 지정
						} else {
							// 1자 종성도 존재하지 않는 경우, 받침이 없거나 기타...
							jong_index = 0;
							first_index = third_index; // 다음 글자의 시작 위치 지정
						}
					}
				}

				// //////////////////////////////////////////////////////////////////////////
				// 완성된 글자 조합
				result += getHangulChar( cho_index, jung_index, jong_index );
			} else if ( 'ㅏ' <= firstChar.charAt( 0 ) && firstChar.charAt( 0 ) <= 'ㅣ' ) { // 모음 { 0x314f~0x3163 }

				int singleMoumCode = getCharIndex( jungsung_str, getSingle( str, first_index ) );

				if ( singleMoumCode != -1 ) {
					// 한 글자의 중성 코드가 존재하는 경우

					if ( first_index + 1 < str.length() ) { // 한글자가 더 있는 경우에만 진행
						int doubleMoumCode = getCharIndex( jungsung_str, getDouble( str, first_index ) );
						// 두 글자 조합의 중성 코드도 존재하는 경우
						if ( doubleMoumCode != -1 ) {
							result += ( jungsung_char[ doubleMoumCode ] );
							first_index = first_index + 2; // 다음 글자의 시작 인덱스 지정
							continue;
						}
					}
					// 한 글자 중성 코드인 경우 이어서...
					result += ( jungsung_char[ singleMoumCode ] );
					first_index = first_index + 1; // 다음 글자의 시작 인덱스 지정
				}
			} else { // 첫 글자가 자음도 모음도 아닌 경우 (영어나 기호 등)
				result += firstChar;
				first_index += 1;
				continue;
			}
		} // while

		return result;
	}

}

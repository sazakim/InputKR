# Processing 한글 입력 Library 


#### Windows10-JAVA2D(Normal) : 한영키 -> 한/영 전환 (키 Released 했을 때, 타이핑)  

#### Windows10-P2D / P3D : Shift + 스페이스바 -> 한/영 전환 (키 Pressed 했을 때 타이핑)

#### OSX-Catalina-JAVA2D/P2D/P3D : CapsLock 등 시스템 설정의 한/영 전환 (키 Pressed 했을 때 타이핑) 





```
// 한글/영어 타이핑 시 자동 호출  
void onTyping( String s ) {
  println( "typing:", s ); 
  typing = s;
}
```
```
// 엔터키 누르면 자동 호출(마지막 문자열로 호출 후 타이핑 중이던 문자열 리셋)  
void onEnter( String s ) {
  println( "completed:", s ); 
  completed = s;
}
```
 
 
> TODO: Windows 상에서 CapsLock이 눌린 상태에서 한글 입력 시 오동작.

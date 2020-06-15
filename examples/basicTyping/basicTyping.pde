import com.sazakim.input.*; 
InputKR inputKR; 

String completed = "";
String typing = "";
float x, y, w; 

PFont font; 
int fontSize = 24; 

void setup() {
  size( 600, 200, P3D );
  font = createFont( "NanumGothic.ttf", fontSize ); 
  textFont(font);

  inputKR = new InputKR( this );
}

void draw() {
  background(0); 

  x = 20; 
  y = height/2; 

  fill(0, 255, 255);
  text( completed, x, 40 );

  textSize( fontSize );
  w = textWidth( typing ) ;

  fill(255);
  text( typing, x, y );

  display_prompt();
}  

void onEnter( String s ) {
  println( "completed:", s ); 
  completed = s;
}

void onTyping( String s ) {
  println( "typing:", s ); 
  typing = s;
}

void display_prompt() {
  if (frameCount % 60 > 30 ) {
    fill(255);
  } else { 
    fill(0);
  }
  rect( x + w, y, fontSize / 2, fontSize / 10 );
}
 

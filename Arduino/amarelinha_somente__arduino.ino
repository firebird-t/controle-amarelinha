#include <CapacitiveSensor.h>
#include <Adafruit_NeoPixel.h>
#define PIN 2
Adafruit_NeoPixel strip = Adafruit_NeoPixel(120, PIN, NEO_GRB + NEO_KHZ800);

CapacitiveSensor cs_30_31 = CapacitiveSensor(30, 31);
CapacitiveSensor cs_32_33 = CapacitiveSensor(32, 33);
CapacitiveSensor cs_34_35 = CapacitiveSensor(34, 35);
CapacitiveSensor cs_36_37 = CapacitiveSensor(36, 37);
CapacitiveSensor cs_38_39 = CapacitiveSensor(38, 39);
CapacitiveSensor cs_40_41 = CapacitiveSensor(40, 41);
CapacitiveSensor cs_52_53 = CapacitiveSensor(52, 53);

#include<SoftwareSerial.h>
#include <ArduinoJson.h>
SoftwareSerial BTSerial(10, 11);

//Controle da amarelinha
DynamicJsonBuffer jsonBuffer;
JsonObject& root1 = jsonBuffer.createObject();
String c = "";
String device;
String address;
bool choose = false;
String valor_botao;
bool game_init = false;
bool touch = false;
int game = 1;
bool ida =  false;
bool volta = false;

//Controle campo minado
int fields[2];
int inicial = 1;
//Controle amarelinha
bool tecla1 = false;
bool tecla2 = false;
bool tecla3 = false;
bool tecla4 = false;
bool tecla5 = false;
bool tecla6 = false;
bool tecla7 = false;
bool tecla8 = false;
bool init_ = false;
bool tecla_escolha1 = false;
bool tecla_escolha2 = false;
bool tecla_escolha3 = false;
bool tecla_escolha4 = false;
bool tecla_escolha5 = false;
bool tecla_escolha6 = false;
bool tecla_escolha7 = false;
bool tecla_escolha8 = false;
bool tecla_controle1 = false;
bool tecla_controle2 = false;
bool tecla_controle3 = false;
bool tecla_controle4 = false;
bool tecla_controle5 = false;
bool tecla_controle6 = false;
bool tecla_controle7 = false;
bool tecla_controle8 = false;
bool tecla_contato1 = false;
bool tecla_contato2 = false;
bool tecla_contato3 = false;
bool tecla_contato4 = false;
bool tecla_contato5 = false;
bool tecla_contato6 = false;
bool tecla_contato7 = false;
bool tecla_contato8 = false;
long total1;
long total2;
long total3;
long total4;
long total5;
long total6;
long total7;
long total8;
String velocidade;
//A resposta para tudo
int tecla_ativa = 42;

//CapacitiveSensor cs_44_45=CapacitiveSensor(44,45);
int red = 0, blue = 255, green = 0;
void setup() {
  strip.begin();
  for (int i = 0; i <= 120; i++) {
    strip.setPixelColor(i, red, green, blue);
  }
  strip.show(); // Initialize all pixels to 'off'

  cs_30_31.set_CS_AutocaL_Millis(0xFFFFFFFF);
  cs_32_33.set_CS_AutocaL_Millis(0xFFFFFFFF);
  cs_34_35.set_CS_AutocaL_Millis(0xFFFFFFFF);
  cs_36_37.set_CS_AutocaL_Millis(0xFFFFFFFF);
  cs_38_39.set_CS_AutocaL_Millis(0xFFFFFFFF);
  cs_40_41.set_CS_AutocaL_Millis(0xFFFFFFFF);
  cs_52_53.set_CS_AutocaL_Millis(0xFFFFFFFF);
  //cs_54_55.set_CS_AutocaL_Millis(0xFFFFFFFF);
  //cs_44_45.set_CS_AutocaL_Millis(0xFFFFFFFF);
  // turn off autocalibrate on channel 1 - just as an example
  
  Serial.begin(115200);
  Serial.println("starting...");
  //Set to HC-05 default baud rate, found using AT+UART.  It is usually 38400.
  
  BTSerial.begin(38400);
  root1["pedra_ok"] = "ok";
  
  delay(2000);
  for (int i = 0; i <= 120; i++) {
    strip.setPixelColor(i, 0, 0, 0);
  }

  inicia_jogo(1,"");
  strip.show();
  reset();
}

void loop() {

  total1 =  cs_30_31.capacitiveSensor(30) * 10;
  total2 =  cs_32_33.capacitiveSensor(30);
  total3 =  cs_34_35.capacitiveSensor(30);
  total4 =  cs_36_37.capacitiveSensor(30) * 20;
  total5 =  cs_38_39.capacitiveSensor(30) * 20;
  total6 =  cs_40_41.capacitiveSensor(30) * 20;
  total7 =  cs_52_53.capacitiveSensor(30) * 20;
  //long total8 =  cs_53_54.capacitiveSensor(100)*150;

  //Checando portas seriais
  if (BTSerial.available() > 0) {
    char tmp = BTSerial.read();
    c += tmp;
    if (tmp == '}') {
      //Serial.println(c);
      json_inpt(c);
      c = "";
    }
  }
  // Read from the Serial Monitor and send to the Bluetooth module
  if (Serial.available()) {
    BTSerial.write(Serial.read());
  }

  if(game == 1){
     amarelinha();  
  }
  else if(game == 2){
     campo_minado();
  }else if(game == 3){
     memoriza(); 
  }
  //delay(100);
  //logs(tecla_ativa);
  //logs(3);
  //Serial.println("ok");
}

void liga(int escolha, int red, int green, int blue) {
  reset();
  switch (escolha) {
    case 1:
      tecla_ativa = 1;
      reset();
      for (int i = 0; i <= 13; i++) {
        strip.setPixelColor(i, red, green, blue);
      }
      reset();
      break;
    case 2:
      tecla_ativa = 2;
      reset();
      for (int i = 28; i <= 42; i++) {
        strip.setPixelColor(i, red, green, blue);
      }
      reset();
      break;
    case 3:
      tecla_ativa = 3;
      reset();
      for (int i = 14; i <= 27; i++) {
        strip.setPixelColor(i, red, green, blue);
      }
      reset();
      break;
    case 4:
      tecla_ativa = 4;
      reset();
      for (int i = 44; i <= 57; i++) {
        strip.setPixelColor(i, red, green, blue);
      }
      reset();
      break;
    case 5:
      tecla_ativa = 5;
      reset();
      for (int i = 72; i <= 86; i++) {
        strip.setPixelColor(i, red, green, blue);
      }
      reset();
      break;
    case 6:
      tecla_ativa = 6;
      reset();
      for (int i = 58; i <= 71; i++) {
        strip.setPixelColor(i, red, green, blue);
      }
      reset();
      break;
    case 7:
         tecla_ativa = 7;
         reset();
         volta = true;
         for (int i = 87; i <= 100; i++) {
            strip.setPixelColor(i, red, green, blue);
          }
         reset();
      break;
    case 8:
      break;
    default:
      Serial.println();
  }
  strip.show();
  reset();
}

void logs(int escolha) {
  switch (escolha) {
    case 1:
      Serial.println(total1);
      Serial.println(tecla_ativa);
      break;
    case 2:
      Serial.println(total2);
      Serial.println(tecla_ativa);
      break;
    case 3:
      Serial.println(total3);
      Serial.println(tecla_ativa);
      break;
    case 4:
      Serial.println(total4);
      Serial.println(tecla_ativa);
      break;
    case 5:
      Serial.println(total5);
      Serial.println(tecla_ativa);
      break;
    case 6:
      Serial.println(total6);
      Serial.println(tecla_ativa);
      break;
    case 7:
      Serial.println(total7);
      Serial.println(tecla_ativa);
      break;
    case 8:
      Serial.println(total8);
      Serial.println(tecla_ativa);
      break;
    default:
      touch = true;
  }
}

void json_inpt(String input) {
  Serial.println(input);
  JsonObject& root = jsonBuffer.parseObject(input);
  
  String valor_botao_tmp = root["valor_botao"];
  String modo = root["modo"];
  String encerra = root["fim"];
  reset();

  
  if (game_init) {
    String tmp = input.substring(16, 17);
    Serial.println(tmp);
    int inter_ = tmp.toInt();
    Serial.println("Enviando confirmaÃ§Ã£o...");
    touch = true;
    liga(inter_, 0, 0, 255);
  }

  reset();
  if (choose == false) {
    String jogo = root["jogo"];
    //Amarelinha
    if (jogo == "1") {
      //Serial.println("1");
      reset();
      inicia_jogo(1, "");
    }

    //Campo minado
    if (jogo == "2") {
      Serial.println("2");
      reset();
      Serial.println("Iniciando o jogo do campo minado");
      inicia_jogo(2, modo);
    }

    if (jogo == "3") {
      Serial.println("3");
      reset();
      inicia_jogo(3, modo);
    }

    reset();
    int quant_users = root["quant_users"];
    Serial.println("Jogo Iniciado");
    reset();
    choose = true;
    game_init = true;
    reset();
  
    if(encerra == "fim"){
      game = 0;
      game_init = false;
      choose = false;  
    }
    
  }

  reset();
}

void inicia_jogo(int escolha, String modo) {

  if(escolha == 1) {
    game = 1;
    sorteio(1, "");
  }
  else if(escolha == 2) {
    game = 2;
    sorteio(2, modo);
  }
  else if(escolha == 3) {
    game = 3;
    sorteio(3, modo);
  }

}

void sorteio(int jogo,String modo) {

   if(jogo == 1){
      
        int piece = 2;
//      fields[0] = random(2,4);
//      desliga(fields[0]);
//      Serial.println("nÃºmero escolhido 1");
//      Serial.println(fields[0]);
//      liga(fields[0], random(0,255), random(0,255), random(0,255));
      
        int contador = 0;
        bool check = false;
        int temp = 0;
        
        while(contador < piece){
            temp = random(2,6);
            for(int i = 0; i < piece; i++){
                check = false;
                if(temp == fields[i]){
                   desliga(fields[i]);
                   check = true;
                   break;
                }
            }
            
            if(!check){
                Serial.println("Jogo 1 + nÃºmero escolhido");
                Serial.println(temp);          
                fields[contador] = temp;
                true_tecla(fields[contador]);
                contador++;
            }
        }

                
            if(!tecla_escolha1){
               tecla1 = true;
               liga(1,0,0,255);
            }
            if(!tecla_escolha2){
               tecla2 = true;
               liga(2,0,0,255);
            }
            if(!tecla_escolha3){
               tecla3 = true;
               liga(3,0,0,255);
            }
            if(!tecla_escolha4){
               tecla4 = true;
               liga(4,0,0,255);
            }
            if(!tecla_escolha5){
               tecla5 = true;
               liga(5,0,0,255);
            }
            if(!tecla_escolha6){
               tecla6 = true;
               liga(6,0,0,255);
            }
            if(!tecla_escolha7){
               tecla7 = true;
               liga(7,0,0,255);
            }
            if(!tecla_escolha8){
               tecla8 = true;
               liga(8,0,0,255);
            }   
            
        
   }
   
   //Campo minado 
   if(jogo == 2){
      int piece;
      
      if(modo == "1"){
        piece = 1;  
        fields[0] = random(1,7);
      }
      else if(modo == "2"){
        piece = 2;
        fields[0] = random(1,7);
      }
      else if(modo == "3"){
        piece = 3;
        fields[0] = random(1,7);
      }

      Serial.println("nÃºmero escolhido 1");
      Serial.println(fields[0]);
      liga(fields[0],random(0,255),random(0,255),random(0,255));
      if(piece > 1){
        int contador = 1;
        bool check = false;
        int temp = 0;
        while(contador < piece){
            temp = random(1,7);
            for(int i = 0; i < piece; i++){
                check = false;
                if(temp == fields[i]){
                   check = true;
                   break;
                }
            }
            
            if(!check){
                Serial.println("nÃºmero escolhido 2");
                Serial.println(temp);
                liga(temp,random(0,255),random(0,255),random(0,255));          
                fields[contador] = temp;
                contador++;
            }
        }

        
      }
    
   }
   
   //Memoriza
   if(jogo == "3"){
    
   }
}

void reset() {
  cs_30_31.reset_CS_AutoCal();
  cs_34_35.reset_CS_AutoCal();
  cs_36_37.reset_CS_AutoCal();
  cs_38_39.reset_CS_AutoCal();
  cs_40_41.reset_CS_AutoCal();
  cs_52_53.reset_CS_AutoCal();
  cs_32_33.reset_CS_AutoCal();
}

void false_all() {
  tecla1 = false;
  tecla2 = false;
  tecla3 = false;
  tecla4 = false;
  tecla5 = false;
  tecla6 = false;
  tecla7 = false;
  tecla8 = false;
}

bool check_false_all(){
  if(tecla1){
     return false;
  }
  if(tecla2){
     return false;
  }
  if(tecla3){
     return false;
  }
  if(tecla4){
     return false;
  }
  if(tecla5){
     return false;
  }
  if(tecla6){
     return false;
  }
  if(tecla7){
     return false;
  }
  if(tecla8){
     return false;
  }

  return true;
}

void desliga(int escolha) {
  switch (escolha) {
    case 1:
      for (int i = 0; i <= 13; i++) {
        strip.setPixelColor(i, 0, 0, 0);
      }
      break;
    case 2:
      for (int i = 28; i <= 42; i++) {
        strip.setPixelColor(i, 0, 0, 0);
      }
      break;
    case 3:

      for (int i = 14; i <= 27; i++) {
        strip.setPixelColor(i, 0, 0, 0);
      }
      break;
    case 4:
      for (int i = 44; i <= 57; i++) {
        strip.setPixelColor(i, 0, 0, 0);
      }
      break;
    case 5:

      for (int i = 72; i <= 86; i++) {
        strip.setPixelColor(i, 0, 0, 0);
      }
      break;
    case 6:

      for (int i = 58; i <= 71; i++) {
        strip.setPixelColor(i, 0, 0, 0);
      }
      break;
    case 7:
      for (int i = 87; i <= 100; i++) {
        strip.setPixelColor(i, 0, 0, 0);
      }

      //Serve para informa ao android para passar o proximo jogador
      BTSerial.write(root1.printTo(BTSerial));
      Serial.println("Enviada..." + escolha);
      break;
    case 8:
      break;
    default:
      Serial.println("ok");
  }

  if(game == 1){
    false_all();
  }
   
  reset();
  strip.show();
}


void true_tecla(int escolha) {
  Serial.println("desliga" + escolha);
  reset();
  switch (escolha) {
    case 1:
      tecla_escolha1 = true;
      break;
    case 2:
      tecla_escolha2 = true;
      break;
    case 3:
      tecla_escolha3 = true;
      break;
    case 4:
      tecla_escolha4 = true;
      break;
    case 5:
      tecla_escolha5 = true;
      break;
    case 6:
      tecla_escolha6 = true;
      break;
    case 7:
      tecla_escolha7 = true;
      break;
    case 8:
      tecla_escolha8 = true;
      break;
    default:
      Serial.println("ok");
  }

  if(game == 1){
    false_all();
  }
   
  reset();
  tecla_ativa = 25;
  touch = false;
  strip.show();
}

void amarelinha(){
   //Serial.println("ok");

   if(!init_){
      liga(1,random(255),random(255),random(255));
      liga(7,random(255),random(255),random(255));
      init_ = true;
      ida = true;
      volta = false;
   }

   if (total1 >= 4000) {
      if(tecla_escolha1 && tecla_controle1){
         liga(1,255,0,0); 
         tecla_controle1 = true;
      }else{
         tecla1 = true;
         liga(1,random(255),random(255),random(255));
      }
      reset();
      digitalWrite(13, HIGH);
  }else{
      if(tecla_escolha1 && tecla_controle1){
         desliga(1); 
         tecla_controle1 = false;
      }  
  }

  if (total3 >= 4000) {
      if(tecla_escolha3){
         liga(3,255,0,0); 
         tecla_controle3 = true;
      }else{
         tecla3 = true;
         liga(3,random(255),random(255),random(255));
      }  
      reset();
      digitalWrite(13, HIGH);
  }else{
      if(tecla_escolha3 && tecla_controle3){
         desliga(3); 
         tecla_controle3 = false;
      }  
  }

  if (total2 > 20000) {
      if(tecla_escolha2){
         liga(2,255,0,0);
         tecla_controle2 = true;
      }else{
        tecla2 = true;
         liga(2,random(255),random(255),random(255));
      }
      reset();
      digitalWrite(13, HIGH);
  }else{
      if(tecla_escolha2 && tecla_controle2 ){
         desliga(2);
         tecla_escolha2 = false;
      }  
  }

  if (total4 > 4000) {
      if(tecla_escolha4){
         tecla_controle4 = true;
         liga(4,255,0,0); 
      }else{
        tecla4 = true;
         liga(4,random(255),random(255),random(255));
      }
      reset();
      digitalWrite(13, HIGH);
      reset();
  }else{ 
      if(tecla_escolha4 && tecla_controle4){
         desliga(4);
         tecla_controle4 = false;
      }  
  }

  if (total6 > 4000) {
      if(tecla_escolha6){
         liga(6,255,0,0);
         tecla_controle6 = true;
      }else{
         tecla6 = true; 
         liga(6,random(255),random(255),random(255));
      }
      reset();
      digitalWrite(13, HIGH);
  }else{ 
      if(tecla_escolha6 && tecla_controle6){
         desliga(6);
         tecla_controle6 = false; 
      }  
  }

  if (total5 >= 4000) {
      if(tecla_escolha5){
         liga(5,255,0,0);
         tecla_controle5 = true;
      }else{
         tecla5 = true;
         liga(5,random(255),random(255),random(255));
      }
      reset();
      digitalWrite(13, HIGH);
  }else{ 
      if(tecla_escolha5 && tecla_controle5){
         desliga(5); 
         tecla_controle5 = false;
      }  
  }

  if (total7 >= 4000) {
      if(tecla_escolha7){
         liga(7,255,0,0);
         tecla_controle7 = true;
         
      }else{
         tecla7 = true;
         if(checa_ordem()){
            ida = false;
            volta = true;
            Serial.println("Ida Completa");
         }else{
            Serial.println("Falha");
         }
         liga(7,random(255),random(255),random(255));
      }
      //false_all();    
      digitalWrite(13, HIGH);
  }


} 

void campo_minado(){
  if (total1 >= 10000 && check_fields(1)) {
    // Serial.print(1);
      reset();
      tecla1 = true;
      desliga(1);
      digitalWrite(13, HIGH);
  }

  if (total3 >= 10000 && check_fields(3)) {
    if (!tecla3) {
      reset();
      tecla3 = true;
      desliga(3);
      digitalWrite(13, HIGH);
    }
  }

  if (total2 > 10000 && check_fields(2)) {
    if (!tecla2) {
      reset();
      tecla2 = true;
      desliga(2);
      digitalWrite(13, HIGH);
    }
  }

  if (total4 > 10000 && check_fields(4)) {
    if (!tecla4) {
      reset();
      tecla4 = true;
      desliga(4);
      digitalWrite(13, HIGH);
    }
  }

  if (total6 > 20000 && check_fields(6)) {
    if (!tecla6) {
      reset();
      tecla6 = true;
      desliga(6);
      digitalWrite(13, HIGH);
    }
  }

  if (total5 >= 20000 && check_fields(5)) {
    if (!tecla5) {
      reset();
      tecla5 = true;
      desliga(5);
      digitalWrite(13, HIGH);
    }
  }

  if (total7 >= 22000 && check_fields(7)) {
    if (!tecla7) {
      reset();
      tecla7 = true;
      desliga(7);
      digitalWrite(13, HIGH);
    }
  }

}

void memoriza(){
  
}

bool check_fields(int escolha){
  for(int i = 0; i <  sizeof(fields); i++){
      if(escolha == fields[i]){
          return true;
      }
 }
  return false;
}

bool checa_ordem(){
  if(ida == true && volta == false){
    Serial.println("chamado");
    String positions[8];
    int i = 0;
    if(tecla1 && !tecla_escolha1){
        Serial.println("1 ok" + i);
        positions[i] = "ok";
        i++;
    }else{
       Serial.println("1 falha");
    }
    
    if(tecla2 && !tecla_escolha2){
        if(positions[i - 1] == "ok"){
          Serial.println("2 ok");
          positions[i] = "ok";
          i++;
        }else{
          Serial.println("2 falha");
          return false;      
        }
    }
    
    if(tecla3 && !tecla_escolha3){
        if(positions[i - 1] == "ok"){
          Serial.println("3 ok");
          positions[i] = "ok";
          i++;
        }else{
          Serial.println("3 falha");
          return false;      
        }
    }
    if(tecla4 && !tecla_escolha4){
        if(positions[i - 1] == "ok"){
          Serial.println("4 ok");
          positions[i] = "ok";
          i++;
        }else{
          Serial.println("4 falha");
          return false;      
        }
    }
    if(tecla5 && !tecla_escolha5){
        if(positions[i - 1] == "ok"){
          Serial.println("5 ok");
          positions[i] = "ok";
          i++;
        }else{
          Serial.println("5 falha");
          return false;      
        }
    }
    if(tecla6 && !tecla_escolha6){
        if(positions[i - 1] == "ok"){
          Serial.println("6 ok");
          positions[i] = "ok";
          i++;
        }else{
          Serial.println("6 falha");
          return false;      
        }
    }
    if(tecla7 && !tecla_escolha7){
        if(positions[i - 1] == "ok"){
          Serial.println("7 ok");
          positions[i] = "ok";
          i++;
          return true;
        }else{
          Serial.println("7 falha");
          return false;      
        }
    }
    if(tecla8 && !tecla_escolha8){
        if(positions[i - 1] == "ok"){
          positions[i] = "ok";
          i++;
          return true;
        }else{
          return false;      
        }
    }
  }
  if(volta == true){
    int i = 8;
    String positions[8];
    if(tecla1 && tecla_escolha1){
        positions[i] = "ok";
        i--;
    }
    
    if(tecla2 && tecla_escolha2){
        if(positions[i + 1] == "ok"){
          positions[i + 1] = "ok";
          i--;
        }else{
          return false;      
        }
    }
    
    if(tecla3 && tecla_escolha3){
        if(positions[i + 1] == "ok"){
          positions[i + 1] = "ok";
          i--;
        }else{
          return false;      
        }
    }
    if(tecla4 && tecla_escolha4){
        if(positions[i + 1] == "ok"){
          positions[i] = "ok";
          i--;
        }else{
          return false;      
        }
    }
    if(tecla5 && tecla_escolha5){
        if(positions[i + 1] == "ok"){
          positions[i] = "ok";
          i--;
        }else{
          return false;      
        }
    }
    if(tecla6 && tecla_escolha6){
        if(positions[i + 1] == "ok"){
          positions[i] = "ok";
          i--;
        }else{
          return false;      
        }
    }
    if(tecla7 && tecla_escolha7){
        if(positions[i + 1] == "ok"){
          positions[i] = "ok";
          i--;
          return true;
        }else{
          return false;      
        }
    }
    if(tecla8 && tecla_escolha8){
        if(positions[i + 1] == "ok"){
          positions[i] = "ok";
          i--;
          return true;
        }else{
          return false;      
        }
    }
  }
}


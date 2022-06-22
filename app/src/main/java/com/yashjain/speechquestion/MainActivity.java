package com.yashjain.speechquestion;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeechService;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.yashjain.speechquestion.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private TextToSpeech TTS;
    ArrayList<String> res;
    String ans="";
    String correctAns="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        TTS=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i==TextToSpeech.SUCCESS){
                    int result=TTS.setLanguage(Locale.US);

                    if(result==TextToSpeech.LANG_MISSING_DATA
                    || result==TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.d("TTS","Language is not supported");
                        Toast.makeText(MainActivity.this, "Language is not supported", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Log.e("TTS","error");
                    Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.listeningText.setVisibility(View.INVISIBLE);
                binding.answerText.setText("");
                binding.question.setVisibility(View.INVISIBLE);
                binding.question.setText("");
                createRandomQuestion();
            }
        });

        binding.listeningText.setOnClickListener(view -> {
            getSpeechInput();
        });
    }

    private void speak(String text) {
        TTS.speak(text,TextToSpeech.QUEUE_FLUSH,null);
        binding.listeningText.setVisibility(View.VISIBLE);
    }

    private void createRandomQuestion() {
        int a=(int)(Math.random()*(9-1+1)+1);
        int b=(int)(Math.random()*(9-1+1)+1);
        int ans=a*b;
        correctAns=Integer.toString(ans);
        binding.question.setVisibility(View.VISIBLE);
        binding.questionNumber.setVisibility(View.VISIBLE);
        binding.question.setText("What is "+a+" times "+b+" ?");
        speak(binding.question.getText().toString());
    }

    @Override
    protected void onDestroy() {
        if(TTS!=null){
            TTS.stop();
            TTS.shutdown();
        }
        super.onDestroy();
    }

    public void getSpeechInput(){
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,5);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Start Speaking...");
        try {
            startActivityForResult(intent, 111);
        }
        catch (Exception e) {
            Toast
                    .makeText(MainActivity.this, " " + e.getMessage(),
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==111){
            res=new ArrayList<>();
            if(resultCode==RESULT_OK && data!=null){
                res=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                ans=res.get(0);
                binding.answerText.setVisibility(View.VISIBLE);
                binding.answerText.setText("Your Ans: "+ans);
                if(ans.equals(correctAns)){
                    speak("Correct!");
                    Toast.makeText(MainActivity.this, "Correct!", Toast.LENGTH_SHORT).show();

                }else{
                    speak("Incorrect!");
                    Toast.makeText(MainActivity.this, "Incorrect!", Toast.LENGTH_SHORT).show();

                }

            }
        }
    }
}
package com.example.exclusivekeyboard;

import android.annotation.SuppressLint;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

@SuppressLint("Registered")
public class ExclusiveIME extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView keyboardView;
    private Keyboard keyboard;

    private boolean caps = false;

    /*STEP 1 (ՔԱՅԼ 1)
     * When the keyboard is created, the onCreateInputView method method is called. All Service variables can be initialized here.
     * Ստեղնաշարի ստեղծման դեպքում կանչվում է onCreateInputView մեթոդը: Ծառայության բոլոր փոփոխականները կարող են նախաստորագրվել(инициализированы) այստեղ:*/
    @Override
    public View onCreateInputView() {
        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
        keyboard = new Keyboard(this, R.xml.keys);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);
        return keyboardView;
    }

    /*STEP 2 (ՔԱՅԼ 2)
     * We create a method that plays a sound when a key is pressed. We use the AudioManager class to play sound.
     * The Android SDK includes several standard default sound effects for keystrokes, and they are used in the playClick method.
     * Մենք ստեղծում ենք մի մեթոդ, որը ձայն է նվագում, երբ ստեղնը սեղմվում է: Մենք օգտագործում ենք AudioManager դասը `ձայն հնչելու համար:
     *  Android SDK- ն ներառում է մի քանի ստանդարտ ձայնային էֆեկտներ `սեղմելով ստեղները, և դրանք օգտագործվում են playClick մեթոդով:
     * */
    private void playClick(int keyCode) {
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        switch (keyCode) {
            case 32:
                assert am != null;
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case Keyboard.KEYCODE_DONE:
            case 10:
                assert am != null;
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                assert am != null;
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default:
                assert am != null;
                am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }
    }


    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    /*STEP 3 (ՔԱՅԼ 3)
     * Will update onKey so that our application can interact with input fields (usually EditText) of other applications.
     * Թարմացնել onKey-ը, որպեսզի մեր ծրագիրը կարողանա փոխազդել այլ ծրագրերի մուտքային դաշտերի (սովորաբար ՝ EditText) հետ:*/
    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        /* The getCurrentInputConnection method is used to get a connection to the input field of another application.
         * GetCurrentInputConnection մեթոդը օգտագործվում է մեկ այլ հավելվածի մուտքային դաշտի հետ կապ ստանալու համար:*/
        InputConnection inputConnection = getCurrentInputConnection();
        playClick(primaryCode);
        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                /* DeleteSurroundingText to delete one or more characters of the input field
                 * DeleteSurroundingText՝ մուտքային դաշտի մեկ կամ մի քանի նիշ հեռացնելու համար*/
                inputConnection.deleteSurroundingText(1, 0);
                break;
                /* If the code is KEYCODE_SHIFT, the caps value changes, and the keypress state is updated using the setShifted method.
                    When the state changes, keystrokes must be redrawn so that the labels of the keys are updated.
                    InvalidateAllKeys is used to redraw all keys.
                  Եթե կոդը KEYCODE_SHIFT է, ապա caps արժեքը փոխվում է, իսկ ստեղնաշարի վիճակը թարմացվում է setShifted մեթոդով:
                    Երբ սեխմվող ստեղնի վիճակը փոխվի, պետք է ստեղները վերափոխվեն, որպեսզի ստեղների պիտակները թարմացվեն:
                    InvalidateAllKeys- ը օգտագործվում է բոլոր բանալիների վերափոխման համար:

                    caps="true" then the character is converted to uppercase.
                    caps="true  ապա սիմվոլը վերածվում է մեծատառի:"*/
            case Keyboard.KEYCODE_SHIFT:
                caps = !caps;
                keyboard.setShifted(caps);
                keyboardView.invalidateAllKeys();
                break;
            case Keyboard.KEYCODE_DONE:
                /* SendKeyEvent to send events, such as KEYCODE_ENTER, to an external application
                 * Ուղարկել KeyEvent՝ իրադարձություններ, ինչպիսիք են KEYCODE_ENTER-ը, արտաքին ծրագրին ուղարկելու համար*/
                inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
            default:
                /*UpperCase
                 * Մեծատառ*/
                char code = (char) primaryCode;
                if (Character.isLetter(code) && caps) {
                    code = Character.toUpperCase(code);
                }
                /* CommitText to add one or more characters to the input field
                 * commitText՝ մուտքային դաշտում մեկ կամ մի քանի նիշ ավելացնելու համար*/
                inputConnection.commitText(String.valueOf(code), 1);
        }
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}

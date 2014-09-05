/*
 * Remoteroid - A remote control solution for Android platform, 
 * including handy file transfer and notify-to-PC.
 * Copyright (C) 2012 Taeho Kim(jyte82@gmail.com), Hyomin Oh(ohmnia1112@gmail.com), 
 * Hongkyun Kim(godgjdgjd@nate.com), Yongwan Hwang(singerhwang@gmail.com)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package org.secmem.remoteroid.ime;

import java.util.List;
import java.util.Locale;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.util.Util;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.os.Build;
import android.text.method.MetaKeyKeyListener;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Toast;

public class HardKeyboard extends InputMethodService {
    
    /**
     * This boolean indicates the optional example code for performing
     * processing of hard keys in addition to regular text generation
     * from on-screen interaction.  It would be used for input methods that
     * perform language translations (such as converting text entered on 
     * a QWERTY keyboard to Chinese), but may not be used for input methods
     * that are primarily intended to be used for on-screen text entry.
     */
    static final boolean PROCESS_HARD_KEYS = true;
    public static final String DEF_CHARSET = "UTF-8";
    
    private StringBuilder mComposing = new StringBuilder();
    private long mMetaState;
    private boolean isCapsLockEnabled = false;
    
    private HangulAutomata mHangulAutomata = new HangulAutomata();
    private boolean isHangulMode = true;
    
    private String mWordSeparators;
    
    /**
     * Main initialization of the input method component.  Be sure to call
     * to super class.
     */
    @Override public void onCreate() {
        super.onCreate();
        mWordSeparators = getResources().getString(R.string.word_separators);
        
    }


    /**
     * This is the main point where we do our initialization of the input method
     * to begin operating on an application.  At this point we have been
     * bound to the client, and are now receiving all of the detailed information
     * about the target of our edits.
     */
    @Override public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        mHangulAutomata.reset();
        
        // Detect current locale and set default input language
        Locale loc = Util.InputMethod.getLastLocale(getApplicationContext());
        if(loc.equals(Locale.KOREA) || loc.equals(Locale.KOREAN))
        	isHangulMode = true;
        else
        	isHangulMode = false;
        
        // Show indicator
        showStatusIcon(isHangulMode? R.drawable.ico_hangul : R.drawable.ico_english);
        
        // Reset our state.  We want to do this even if restarting, because
        // the underlying state of the text editor could have changed in any way.
        mComposing.setLength(0);
        
        if (!restarting) {
            // Clear shift states.
            mMetaState = 0;
        }
        
    }

    /**
     * This is called when the user is done editing a field.  We can use
     * this to reset our state.
     */
    @Override public void onFinishInput() {
        super.onFinishInput();
        
        // Clear current composing text and candidates.
        mComposing.setLength(0);
        mHangulAutomata.reset();
    }
    
    /**
     * Deal with the editor reporting movement of its cursor.
     */
    @Override public void onUpdateSelection(int oldSelStart, int oldSelEnd,
            int newSelStart, int newSelEnd,
            int candidatesStart, int candidatesEnd) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
                candidatesStart, candidatesEnd);
        
        // If the current selection in the text view changes, we should
        // clear whatever candidate text we have.
        if (mComposing.length() > 0 && (newSelStart != candidatesEnd
                || newSelEnd != candidatesEnd)) {
            mComposing.setLength(0);
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                ic.finishComposingText();
                if(isHangulMode){
                	mHangulAutomata.reset();
                }
            }
        }
    }
    
    /**
     * This translates incoming hard key events in to edit operations on an
     * InputConnection.  It is only needed when using the
     * PROCESS_HARD_KEYS option.
     */
    private boolean translateKeyDown(int keyCode, KeyEvent event) {
        mMetaState = MetaKeyKeyListener.handleKeyDown(mMetaState,
                keyCode, event);
        int c = event.getUnicodeChar(MetaKeyKeyListener.getMetaState(mMetaState));
        mMetaState = MetaKeyKeyListener.adjustMetaAfterKeypress(mMetaState);
        InputConnection ic = getCurrentInputConnection();
        if (c == 0 || ic == null) {
            return false;
        }

        if ((c & KeyCharacterMap.COMBINING_ACCENT) != 0) {
            c = c & KeyCharacterMap.COMBINING_ACCENT_MASK;
        }
        
        if (mComposing.length() > 0) {
            char accent = mComposing.charAt(mComposing.length() -1 );
            int composed = KeyEvent.getDeadChar(accent, c);

            if (composed != 0) {
                c = composed;
                mComposing.setLength(mComposing.length()-1);
            }
        }
        
        onKey(c, null);
        
        return true;
    }
    
    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     */
    @SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            	if(this.isInputViewShown()){
            		mHangulAutomata.reset();
            		this.hideWindow();
            	}
                break;
                
            case KeyEvent.KEYCODE_CAPS_LOCK:
            	if(Build.VERSION.SDK_INT <=9){
            		if((event.getMetaState()&KeyEvent.META_CAPS_LOCK_ON)==KeyEvent.META_CAPS_LOCK_ON){
            			isCapsLockEnabled = true;
            		}else{
            			isCapsLockEnabled = false;
            		}
            	}else{
            		isCapsLockEnabled = event.isCapsLockOn();
            	}
            	break;
       
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_UP:
            	if(isHangulMode){
            		mHangulAutomata.reset();
            	}
            	return false;
                
            case KeyEvent.KEYCODE_DEL:
                // Special handling of the delete key: if we currently are
                // composing text for the user, we want to modify that instead
                // of let the application to the delete itself.
                if (mComposing.length() > 0) {
                    onKey(Keyboard.KEYCODE_DELETE, null);
                    return true;
                }
                break;
                
            case KeyEvent.KEYCODE_ENTER:
            	if(isHangulMode)
            		mHangulAutomata.reset();
                // Let the underlying text editor always handle these.
                return false;
 
                
            default:
                // For all other keys, if we want to do transformations on
                // text being entered with a hard keyboard, we need to process
                // it and do the appropriate action.
                if (PROCESS_HARD_KEYS) {
                    if (keyCode == KeyEvent.KEYCODE_SPACE
                            && ((event.getMetaState()&KeyEvent.META_SHIFT_LEFT_ON)==KeyEvent.META_SHIFT_LEFT_ON)) {
                        switchLanguage();
                        return true; // Consume this event
                    }
                    
                    if(keyCode==KeyEvent.KEYCODE_V &&
                    		((event.getMetaState()&KeyEvent.META_CTRL_LEFT_ON)==KeyEvent.META_CTRL_LEFT_ON)){
                    	if(Build.VERSION.SDK_INT>=11){
	                    	ClipboardManager manager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
	                    	
	                    	if(manager.getPrimaryClip().getItemCount()>0){
	                    		onText(manager.getPrimaryClip().getItemAt(0).getText());
	                    	}
                    	}else{
							android.text.ClipboardManager manager = (android.text.ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                    		if(manager.hasText()){
                    			onText(manager.getText());
                    		}
                    	}
                    	return true;
                    }
                    		
                    
                    if (translateKeyDown(keyCode, event)) {
                        return true;
                    }
                }
        }
        
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     */
    @Override public boolean onKeyUp(int keyCode, KeyEvent event) {
        // If we want to do transformations on text being entered with a hard
        // keyboard, we need to process the up events to update the meta key
        // state we are tracking.
        if (PROCESS_HARD_KEYS) {

                mMetaState = MetaKeyKeyListener.handleKeyUp(mMetaState,
                        keyCode, event);
            
        }
        
        return super.onKeyUp(keyCode, event);
    }

    @Override
	public void onFinishInputView(boolean finishingInput) {
		super.onFinishInputView(finishingInput);
		mHangulAutomata.reset();
	}


	/**
     * Helper function to commit any text being composed in to the editor.
     */
    private void commitTyped(InputConnection inputConnection) {
        if (mComposing.length() > 0) {
            inputConnection.commitText(mComposing, mComposing.length());
            mComposing.setLength(0);
        }
    }

    /**
     * Helper to update the shift state of our keyboard based on the initial
     * editor state.
     */
    private void updateShiftKeyState(EditorInfo attr) {
        if (attr != null) {
            EditorInfo ei = getCurrentInputEditorInfo();
            if (ei != null && ei.inputType != EditorInfo.TYPE_NULL) {
                getCurrentInputConnection().getCursorCapsMode(attr.inputType);
            }
        }
    }
    
    /**
     * Helper to determine if a given character code is alphabetic.
     */
    private boolean isAlphabet(int code) {
        if (Character.isLetter(code)) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Helper to send a key down / key up pair to the current editor.
     */
    private void keyDownUp(int keyEventCode) {
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }
    
    /**
     * Helper to send a character to the editor as raw key events.
     */
    private void sendKey(int keyCode) {
        switch (keyCode) {
            case '\n':
                keyDownUp(KeyEvent.KEYCODE_ENTER);
                break;
            default:
                if (keyCode >= '0' && keyCode <= '9') {
                    keyDownUp(keyCode - '0' + KeyEvent.KEYCODE_0);
                } else {
                    getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1);
                }
                break;
        }
    }

    // Implementation of KeyboardViewListener

    public void onKey(int primaryCode, int[] keyCodes) {
        if (isWordSeparator(primaryCode)) {
            // Handle separator
            if (mComposing.length() > 0) {
                commitTyped(getCurrentInputConnection());
                if(isHangulMode){
                	mHangulAutomata.reset();
                }
            }
            sendKey(primaryCode);
            updateShiftKeyState(getCurrentInputEditorInfo());
        } else if (primaryCode == Keyboard.KEYCODE_DELETE) {
            handleBackspace();
        } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
            handleShift();
        } else if (primaryCode == Keyboard.KEYCODE_CANCEL) {
            handleClose();
            return;
        } else if (primaryCode == -1000) {
            // Show a menu or somethin'
        } else {
            handleCharacter(primaryCode, keyCodes);
        }
    }

    public void onText(CharSequence text) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        ic.beginBatchEdit();
        if (mComposing.length() > 0) {
            commitTyped(ic);
        }
        ic.commitText(text, 0);
        ic.endBatchEdit();
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    
    public void setSuggestions(List<String> suggestions, boolean completions,
            boolean typedWordValid) {
        if (suggestions != null && suggestions.size() > 0) {
            setCandidatesViewShown(true);
        } else if (isExtractViewShown()) {
            setCandidatesViewShown(true);
        }
        
    }
    
    private void handleBackspace() {
        final int length = mComposing.length();
        if (length > 1) {
            mComposing.delete(length - 1, length);
            // Handle hangul
            if(isHangulMode){
	            int result = mHangulAutomata.deleteCharacter();
				if(result != -1)
					mComposing.append((char)result);
            }
			
            getCurrentInputConnection().setComposingText(mComposing, 1);
        } else if (length > 0) {
        	if(isHangulMode){
        		mHangulAutomata.reset();
        	}
            mComposing.setLength(0);
            getCurrentInputConnection().commitText("", 0);
        } else {
            keyDownUp(KeyEvent.KEYCODE_DEL);
        }
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    private void handleShift() {
        
    }
    
    private void switchLanguage(){
    	mHangulAutomata.reset();
    	// Switch language
    	isHangulMode = !isHangulMode;
    	
    	// Save switched language
    	Util.InputMethod.setLastLocale(getApplicationContext(), isHangulMode ? Locale.KOREAN : Locale.ENGLISH);
    	Toast.makeText(getApplicationContext(), isHangulMode? R.string.ime_mode_kor : R.string.ime_mode_eng, Toast.LENGTH_SHORT).show();
    	showStatusIcon(isHangulMode? R.drawable.ico_hangul : R.drawable.ico_english);
    }
    
    private void handleCharacter(int primaryCode, int[] keyCodes) {
        
        if (isCapsLockEnabled) {
            primaryCode = Character.toUpperCase(primaryCode);
        }
        
        if (isAlphabet(primaryCode)&&!isHangulMode) {
            mComposing.append((char) primaryCode);
            getCurrentInputConnection().setComposingText(mComposing, 1);
            updateShiftKeyState(getCurrentInputEditorInfo());
        }else if(isHangulMode){
        	InputConnection ic = getCurrentInputConnection();
        	int length = mComposing.length();
        	
        	ic.beginBatchEdit();
			 if(mHangulAutomata.getBuffer() != -1 && 0 < length)
			    	mComposing.delete(length - 1, length);
		    int ret[] = mHangulAutomata.appendCharacter(HangulAutomata.toHangulCode(primaryCode));
		    
		    for(int i = 0; i < ret.length - 1; i++){
		    	if(ret[i] != -1)
		    		mComposing.append((char)ret[i]);
		    }
		    ic.commitText(mComposing, 1);
		    mComposing.setLength(0);
		    if(ret[2] != -1){
		    	mComposing.append((char)ret[2]);
		    	ic.setComposingText(mComposing, 1);
		    }
		    ic.endBatchEdit();
        }else{
            getCurrentInputConnection().commitText(
                    String.valueOf((char) primaryCode), 1);
        }
    }

    private void handleClose() {
        commitTyped(getCurrentInputConnection());
        mHangulAutomata.reset();
        requestHideSelf(0);
    }
    
    private String getWordSeparators() {
        return mWordSeparators;
    }
    
    public boolean isWordSeparator(int code) {
        String separators = getWordSeparators();
        return separators.contains(String.valueOf((char)code));
    }
    
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.weex;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;


public class SplashActivity extends AppCompatActivity {
  static {
    System.loadLibrary("thh_jni");
  }

  private native int nativeThh(Console object);
  private native String nativeString(String thh);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);

    final View textView = findViewById(R.id.fullscreen_content);
    ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

    AnimationSet animationSet = new AnimationSet(false);
    animationSet.addAnimation(scaleAnimation);
    animationSet.addAnimation(rotateAnimation);
    animationSet.setDuration(1500);

    animationSet.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {
      }

      @Override
      public void onAnimationEnd(Animation animation) {
       /* startActivity(new Intent(SplashActivity.this, IndexActivity.class));
        finish();*/
      }

      @Override
      public void onAnimationRepeat(Animation animation) {
      }
    });
    textView.startAnimation(animationSet);

    textView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        nativeThh(new Console());
       // String result = nativeString("function f() {var x = 5; if (x == 5) {Console.log('hello world') return 'hello world' }}; f()" );

        String result = nativeString("Console.log('ABC')");
        Log.e("🐷🐷", "result == " + result);
      }
    });
  }
}

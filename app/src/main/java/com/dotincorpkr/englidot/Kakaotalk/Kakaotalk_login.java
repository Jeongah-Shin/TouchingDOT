package com.dotincorpkr.englidot.Kakaotalk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dotincorpkr.englidot.R;
import com.dotincorpkr.englidot.Splash;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

/**
 * Created by wjddk on 2017-01-23.
 */

    public class Kakaotalk_login extends AppCompatActivity {

        SessionCallback callback;

        @Override
        protected void onCreate(Bundle savedInstanceState) {

            //첫번째 액티비티(로그인) 시작 이전에 Splash를 실행합니다.
            startActivity(new Intent(this, Splash.class));

            super.onCreate(savedInstanceState);
            setContentView(R.layout.kakaotalk_login);

            /**카카오톡 로그아웃 요청**/
            //한번 로그인이 성공하면 세션 정보가 남아있어서 로그인창이 뜨지 않고 바로 onSuccess()메서드를 호출합니다.

            UserManagement.requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {
                    //로그아웃 성공 후 하고싶은 내용 코딩 ~
                }
            });

            callback = new SessionCallback();
            Session.getCurrentSession().addCallback(callback);

        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            //간편로그인시 호출 ,없으면 간편로그인시 로그인 성공화면으로 넘어가지 않음
            if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
                return;
            }

            super.onActivityResult(requestCode, resultCode, data);
        }

        private class SessionCallback implements ISessionCallback {

            @Override
            public void onSessionOpened() {

                UserManagement.requestMe(new MeResponseCallback() {

                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        String message = "failed to get user info. msg=" + errorResult;
                        Logger.d(message);

                        ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                        if (result == ErrorCode.CLIENT_ERROR_CODE) {
                            finish();
                        } else {
                            //redirectMainActivity();
                        }
                    }

                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                    }

                    @Override
                    public void onNotSignedUp() {
                    }

                    @Override
                    public void onSuccess(UserProfile userProfile) {
                        //로그인에 성공하면 로그인한 사용자의 일련번호, 닉네임, 이미지url등을 리턴합니다.
                        //사용자 ID는 보안상의 문제로 제공하지 않고 일련번호는 제공합니다.
                        Log.e("UserProfile", userProfile.toString());
                        Intent intent = new Intent(Kakaotalk_login.this, Kakaotalk_login_success.class);
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onSessionOpenFailed(KakaoException exception) {
                // 세션 연결이 실패했을때
            }

        }
    }


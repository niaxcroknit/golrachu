package com.golrachu.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

/**
 * 골라츄 화면 하나짜리 앱.
 * 화면은 assets/index.html 이고, 이 파일은 그걸 띄워 주고
 * 진동, 다크 모드, 상태바, 키보드 높이, 하단 배너 광고를 챙겨요.
 */
public class MainActivity extends Activity {

    private static final int BG_LIGHT = Color.parseColor("#f4f4f3");
    private static final int BG_DARK = Color.parseColor("#141415");

    /** 진짜 광고 단위 ID (AdMob → 골라츄 → 광고 단위) */
    private static final String AD_UNIT_REAL = "ca-app-pub-2486517051650098/2634927181";
    /** 구글이 주는 테스트용 배너 ID. 안드로이드 스튜디오에서 ▶로 실행할 땐 이걸 써서, 내 광고를 내가 누르는 사고를 막아요 */
    private static final String AD_UNIT_TEST = "ca-app-pub-3940256099942544/9214589741";

    private WebView webView;
    private FrameLayout adBox;
    private AdView adView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean dark = isDark();
        int bg = dark ? BG_DARK : BG_LIGHT;

        // 위: 골라츄 화면 / 아래: 배너 광고 자리
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(bg);

        webView = new WebView(this) {
            // 키보드가 올라올 때 웹뷰가 화면을 밀어 올리지 못하게, 스크롤은 늘 맨 위에 고정
            @Override
            protected void onScrollChanged(int l, int t, int oldl, int oldt) {
                super.onScrollChanged(l, t, oldl, oldt);
                if (l != 0 || t != 0) scrollTo(0, 0);
            }
        };
        webView.setBackgroundColor(bg);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webView.setVerticalScrollBarEnabled(false);
        webView.setScrollContainer(false);
        webView.setHorizontalScrollBarEnabled(false);
        // 진동은 웹뷰에서 바로 울려요
        webView.setHapticFeedbackEnabled(true);

        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setSupportZoom(false);
        ws.setBuiltInZoomControls(false);
        ws.setDisplayZoomControls(false);
        ws.setTextZoom(100); // 폰 글자 크기 설정 때문에 화면이 깨지지 않게 고정
        ws.setAllowFileAccess(true); // 앱 안의 화면 파일(assets)을 열기 위해 필요
        ws.setAllowContentAccess(false);

        webView.addJavascriptInterface(new Bridge(), "Android");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if ("file".equals(uri.getScheme())) return false;
                // 앱 밖 링크는 브라우저로 열어요 (지금 화면에는 없음)
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } catch (Exception ignored) {
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // 광고가 아래를 차지하니, 화면 쪽 아래 여백은 줄여서 버튼이 너무 올라가지 않게
                view.evaluateJavascript("document.documentElement.classList.add('has-ad')", null);
            }
        });

        root.addView(webView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        adBox = new FrameLayout(this);
        adBox.setBackgroundColor(bg);
        // 버튼과 광고 사이를 띄워서 실수로 광고를 누르지 않게 (AdMob 정책)
        int gap = Math.round(8 * getResources().getDisplayMetrics().density);
        adBox.setPadding(0, gap, 0, 0);
        root.addView(adBox, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        setContentView(root);

        setupBars(dark, bg);
        applyInsets(root);

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        } else {
            webView.loadUrl("file:///android_asset/index.html");
        }

        // 광고 준비는 화면을 막지 않게 뒤에서 하고, 끝나면 배너를 불러와요
        new Thread(() -> MobileAds.initialize(this, status -> runOnUiThread(() -> root.post(this::loadBanner)))).start();
    }

    /** 화면 폭에 맞춘 하단 고정 배너를 넣어요. */
    @SuppressWarnings("deprecation")
    private void loadBanner() {
        if (isFinishing() || adBox == null || adView != null) return;
        float density = getResources().getDisplayMetrics().density;
        int widthPx = adBox.getWidth();
        if (widthPx <= 0) widthPx = getResources().getDisplayMetrics().widthPixels;
        int widthDp = (int) (widthPx / density);

        AdSize size = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, widthDp);
        adView = new AdView(this);
        adView.setAdUnitId(BuildConfig.DEBUG ? AD_UNIT_TEST : AD_UNIT_REAL);
        adView.setAdSize(size);
        // 광고가 늦게 떠도 화면이 덜컹거리지 않게 자리를 먼저 잡아 둬요
        adBox.setMinimumHeight(size.getHeightInPixels(this) + adBox.getPaddingTop());
        adBox.addView(adView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM));
        adView.loadAd(new AdRequest.Builder().build());
    }

    private boolean isDark() {
        int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return mode == Configuration.UI_MODE_NIGHT_YES;
    }

    /** 상태바·내비게이션바 색과 아이콘 색을 화면에 맞춰요. */
    @SuppressWarnings("deprecation")
    private void setupBars(boolean dark, int bg) {
        Window w = getWindow();
        View decor = w.getDecorView();
        int flags = decor.getSystemUiVisibility();
        // 모든 버전에서 똑같이 화면을 바 뒤까지 펼치고, 여백은 applyInsets 에서 줘요
        if (Build.VERSION.SDK_INT >= 30) {
            w.setDecorFitsSystemWindows(false);
        } else {
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        }
        if (Build.VERSION.SDK_INT < 35) {
            // 안드로이드 15 미만: 바 색을 화면 바탕색과 맞춰요
            w.setStatusBarColor(bg);
            w.setNavigationBarColor(bg);
        }
        if (Build.VERSION.SDK_INT >= 29) {
            w.setNavigationBarContrastEnforced(false);
        }
        if (!dark) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            if (Build.VERSION.SDK_INT >= 26) flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        } else {
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            if (Build.VERSION.SDK_INT >= 26) flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }
        decor.setSystemUiVisibility(flags);

        if (Build.VERSION.SDK_INT >= 30) {
            android.view.WindowInsetsController c = w.getInsetsController();
            if (c != null) {
                int mask = android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                        | android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS;
                c.setSystemBarsAppearance(dark ? 0 : mask, mask);
            }
        }
    }

    /**
     * 안드로이드 15부터는 화면이 상태바·내비게이션바 뒤까지 꽉 차요.
     * 내용이 바에 가리지 않게, 그리고 키보드가 올라오면 그만큼 비켜 주도록 여백을 줘요.
     * 키보드가 떠 있는 동안에는 광고를 숨겨서, 입력칸·버튼이 키보드 바로 위에 붙게 해요.
     */
    @SuppressWarnings("deprecation")
    private void applyInsets(View root) {
        root.setOnApplyWindowInsetsListener((v, insets) -> {
            int l, t, r, b;
            if (Build.VERSION.SDK_INT >= 30) {
                android.graphics.Insets bars = insets.getInsets(
                        WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout());
                android.graphics.Insets ime = insets.getInsets(WindowInsets.Type.ime());
                l = bars.left;
                t = bars.top;
                r = bars.right;
                b = Math.max(bars.bottom, ime.bottom);
                setAdVisible(ime.bottom <= bars.bottom);
            } else {
                l = insets.getSystemWindowInsetLeft();
                t = insets.getSystemWindowInsetTop();
                r = insets.getSystemWindowInsetRight();
                b = insets.getSystemWindowInsetBottom();
                // 키보드가 뜨면 아래 여백이 화면의 1/4보다 커져요
                setAdVisible(b < v.getRootView().getHeight() / 4);
            }
            v.setPadding(l, t, r, b);
            return insets;
        });
    }

    private void setAdVisible(boolean show) {
        if (adBox != null) adBox.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onPause() {
        if (adView != null) adView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null) adView.resume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (webView != null) webView.saveState(outState);
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
            adView = null;
        }
        if (webView != null) {
            webView.removeJavascriptInterface("Android");
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    /** 화면(index.html)에서 window.Android.haptic("...") 으로 부르는 진동. */
    private class Bridge {
        @JavascriptInterface
        public void haptic(final String type) {
            runOnUiThread(() -> {
                if (webView == null) return;
                webView.performHapticFeedback(feedbackFor(type));
            });
        }
    }

    private static int feedbackFor(String type) {
        if (type == null) type = "";
        switch (type) {
            case "tick": // 후보가 휙휙 돌 때
                return Build.VERSION.SDK_INT >= 27
                        ? HapticFeedbackConstants.TEXT_HANDLE_MOVE
                        : HapticFeedbackConstants.CLOCK_TICK;
            case "land": // 하나에 멈췄을 때
                return HapticFeedbackConstants.VIRTUAL_KEY;
            case "no": // 싫어
                return Build.VERSION.SDK_INT >= 30
                        ? HapticFeedbackConstants.REJECT
                        : HapticFeedbackConstants.LONG_PRESS;
            case "yes": // 좋아
                return Build.VERSION.SDK_INT >= 30
                        ? HapticFeedbackConstants.CONFIRM
                        : HapticFeedbackConstants.VIRTUAL_KEY;
            default:
                return HapticFeedbackConstants.KEYBOARD_TAP;
        }
    }
}

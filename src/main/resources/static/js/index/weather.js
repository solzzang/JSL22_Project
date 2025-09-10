// public/js/index/weather.js
(function () {
  // ============================
  // 1) 공통 유틸 & 이모지 매핑
  // ============================
  const WEATHER_EMOJI_BY_CODE = {
    1000:"☀️",1003:"⛅",1006:"☁️",1009:"☁️",
    1183:"🌦️",1186:"🌧️",1189:"🌧️",
    1087:"⛈️",1273:"⛈️",1276:"⛈️",
    1066:"🌨️",1114:"🌨️",1117:"❄️"
  };
  const emoji = c => WEATHER_EMOJI_BY_CODE[c] || "❓";

  async function fetchJson(url, opt = {}) {
    const r = await fetch(url, { cache: "no-store", ...opt });
    if (!r.ok) throw new Error(`HTTP ${r.status}`);
    return r.json();
  }

  // ============================
  // 2) 로그인 사용자 위치 가져오기
  // ============================
  async function getMeLocation() {
    try {
      const j = await fetchJson("/api/me/location");
      const lat = (typeof j.lat === "number") ? j.lat : 35.681236;
      const lon = (typeof j.lon === "number") ? j.lon : 139.767125;
      return { lat, lon, loggedIn: !!j.loggedIn };
    } catch {
      return { lat: 35.681236, lon: 139.767125, loggedIn: false }; // 도쿄 기본값
    }
  }

  // ============================
  // 3) 내부 상태
  // ============================
  let mapRef = null;   // 구글맵 객체
  let userPos = null;  // {lat, lon}
  let marker = null;   // 일반 마커
  let infoWin = null;  // 인포 윈도우(이모지+기온)
  let aborter = null;  // fetch 취소용
  let busy = false;    // 중복 방지

  function clear() {
    if (marker) { marker.setMap(null); marker = null; }
    if (infoWin) { infoWin.close(); infoWin = null; }
    if (aborter) { aborter.abort(); aborter = null; }
    busy = false;
  }

  // ============================
  // 4) 날씨 불러와서 일반 마커 표시
  // ============================
  async function load() {
    if (!mapRef || !userPos || busy) return;
    busy = true;
    try {
      const WEATHER_API_KEY = "c8b2e054755849cda2e51309251009";

      const url =
        `https://api.weatherapi.com/v1/current.json?key=${WEATHER_API_KEY}` +
        `&q=${userPos.lat},${userPos.lon}&lang=ja`;

      aborter = new AbortController();
      const data = await fetchJson(url, { signal: aborter.signal });
      const wx = data.current;
      const loc = data.location;

      const emo = emoji(wx.condition.code);
      const temp = Math.round(wx.temp_c);

      // 이전 표시 정리 후 기본 마커로 표시
      clear();

	  marker = new google.maps.Marker({
	    map: mapRef,
	    position: { lat: userPos.lat, lng: userPos.lon },
	    icon: {
	      url: "http://maps.google.com/mapfiles/ms/icons/red-dot.png",
	      labelOrigin: new google.maps.Point(15, -10) 
	    },
	    label: {
	      text: `${emo} ${temp}°C`,
	      fontSize: "30px",
	      fontWeight: "bold"
	    },
	    title: `${loc.name} / ${wx.condition.text} / ${temp}°C`
	  });
    } catch (e) {
      console.error("[Weather] load error:", e);
      clear();
    } finally {
      busy = false;
    }
  }

  // ============================
  // 5) 외부에서 사용하는 API
  // ============================
  window.WeatherFeature = {
    // 켜기: 맵 연결 → 위치 결정 → 마커 그리기
    async enable(map, opts = {}) {
      mapRef = map;

      if (typeof opts.lat === "number" && typeof opts.lon === "number") {
        userPos = { lat: opts.lat, lon: opts.lon };
      } else {
        const me = await getMeLocation();
        userPos = { lat: me.lat, lon: me.lon };
      }

      await load();
    },

    // 끄기: 마커/요청/인포윈도우 정리
    disable() {
      clear();
      mapRef = null;
      userPos = null;
    },

    // 위치 재조회 후 다시 그림(선택)
    async refresh() {
      if (!mapRef) return;
      const me = await getMeLocation();
      userPos = { lat: me.lat, lon: me.lon };
      await load();
    }
  };

  // ============================
  // 6) Google Maps 콜백
  // ============================
  window.initMap = async function () {
    const map = new google.maps.Map(document.getElementById("map"), {
      center: { lat: 35.681236, lng: 139.767125 },
      zoom: 8
    });
    window._map = map;            // 다른 스크립트에서 접근용(체크박스 토글 등)
  };
})();
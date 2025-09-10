(function () {
  const WEATHER_EMOJI_BY_CODE = {
    1000:"☀️",1003:"⛅",1006:"☁️",1009:"☁️",
    1183:"🌦️",1186:"🌧️",1189:"🌧️",
    1087:"⛈️",1273:"⛈️",1276:"⛈️",
    1066:"🌨️",1114:"🌨️",1117:"❄️"
  };
  const emoji = c => WEATHER_EMOJI_BY_CODE[c] || "❓";
  const fetchJson = async (url, opt={}) => {
    const r = await fetch(url, { cache:"no-store", ...opt });
    if (!r.ok) throw new Error(`HTTP ${r.status}`);
    return r.json();
  };

  let mapRef=null, userPos=null, marker=null, aborter=null, busy=false;

  function clear() {
    if (marker) { marker.map = null; marker = null; }
    if (aborter) { aborter.abort(); aborter = null; }
    busy = false;
  }
  function makePin(emo, tempC) {
    const wrap = document.createElement("div");
    wrap.style.position="relative"; wrap.style.transform="translate(-50%,-100%)"; wrap.style.pointerEvents="none";
    const face=document.createElement("div"); face.textContent=emo; face.style.fontSize="28px"; face.style.lineHeight="28px"; face.style.textAlign="center";
    const badge=document.createElement("div"); badge.textContent=`${Math.round(tempC)}°C`;
    Object.assign(badge.style,{fontSize:"12px",padding:"2px 6px",borderRadius:"12px",background:"rgba(0,0,0,.7)",color:"#fff",position:"absolute",top:"-6px",right:"-18px"});
    wrap.append(face,badge);
    return wrap;
  }
  async function load() {
    if (!mapRef || !userPos || busy) return;
    busy = true;
    try {
      // ※ 운영에서는 키 노출 방지 위해 서버 프록시 권장
      const url = `https://api.weatherapi.com/v1/current.json?key=c8b2e054755849cda2e51309251009&q=${userPos.lat},${userPos.lon}&lang=ja`;
      aborter = new AbortController();
      const data = await fetchJson(url, { signal: aborter.signal });
      const wx = data.current, loc = data.location;
      const pin = makePin(emoji(wx.condition.code), wx.temp_c);
      clear();
      marker = new google.maps.marker.AdvancedMarkerElement({
        map: mapRef,
        position: { lat:userPos.lat, lng:userPos.lon },
        content: pin,
        title: `${loc.name} / ${wx.condition.text} / ${wx.temp_c}°C`
      });
    } catch (e) {
      console.error("[Weather] load error:", e);
      clear();
    } finally { busy = false; }
  }

  window.WeatherFeature = {
    enable(map, {lat, lon}) { mapRef = map; userPos = {lat, lon}; load(); },
    disable() { clear(); mapRef = null; userPos = null; }
  };
})();
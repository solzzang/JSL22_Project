/* ---------- 안내박스 겹침 방지 ---------- */
function updateLegendLayout() {
  const shelter  = document.getElementById("shelter-info");
  const hospital = document.getElementById("hospital-info");
  const BASE = 10;
  const GAP  = 8;

  if (shelter && hospital) {
    const h = hospital.getBoundingClientRect().height || 0;
    hospital.style.bottom = BASE + "px";
    shelter.style.bottom  = (BASE + h + GAP) + "px";
  } else {
    if (hospital) hospital.style.bottom = BASE + "px";
    if (shelter)  shelter.style.bottom  = BASE + "px";
  }
}

function waitLegendsOnce(timeoutMs = 500) {
  const started = Date.now();
  return new Promise(resolve => {
    (function tick() {
      const s = document.getElementById("shelter-info");
      const h = document.getElementById("hospital-info");
      if ((s && s.offsetHeight) || (h && h.offsetHeight) || Date.now() - started > timeoutMs) {
        resolve();
      } else {
        requestAnimationFrame(tick);
      }
    })();
  });
}

/* ---------- 토글 바인딩 ---------- */
document.addEventListener("DOMContentLoaded", () => {
  const weatherToggle  = document.getElementById('weatherToggle');
  const disasterToggle = document.getElementById('disasterToggle');
  const shelterToggle  = document.getElementById('shelterToggle');
  const hospitalToggle = document.getElementById('hospitalToggle'); 

  if (weatherToggle) {
    weatherToggle.addEventListener('change', () => {
      weatherToggle.checked ? WeatherFeature.enable(window._map)
                            : WeatherFeature.disable();
    });
  }

  if (disasterToggle) {
    disasterToggle.addEventListener('change', () => {
      disasterToggle.checked ? DisasterFeature.enable(window._map)
                             : DisasterFeature.disable();
    });
  }

  if (shelterToggle) {
    shelterToggle.addEventListener('change', async () => {
      if (shelterToggle.checked) {
        await ShelterFeature.enable(window._map);
		window._map.setZoom(15);
      } else {
        ShelterFeature.disable();
		window._map.setZoom(12);
      }
      await waitLegendsOnce();
      updateLegendLayout();
    });
  }

  if (hospitalToggle) {
    hospitalToggle.addEventListener('change', async () => {
      if (hospitalToggle.checked) {
        await HospitalOSMFeature.enable(window._map);
		window._map.setZoom(15);
      } else {
        HospitalOSMFeature.disable();
		window._map.setZoom(12);
      }
      await waitLegendsOnce();
      updateLegendLayout();
    });
  }
});
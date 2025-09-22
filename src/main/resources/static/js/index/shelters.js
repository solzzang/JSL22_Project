// shelter.js
(function () {
  let map;
  let shelterMarkers = [];
  let emergencyMarkers = [];

  // ========================
  // CSV 파서 (간단 버전)
  // ========================
  function parseCSV(text) {
    const lines = text.trim().split("\n");
    const headers = lines[0].split(",");
    return lines.slice(1).map(line => {
      const cols = line.split(",");
      const obj = {};
      headers.forEach((h, i) => {
        obj[h.trim()] = cols[i] ? cols[i].trim() : "";
      });
      return obj;
    });
  }

  // ========================
  // 사용자 위치 API (/api/me/location)
  // ========================
  async function getUserLocation() {
    try {
      const res = await fetch("/api/me/location");
      if (!res.ok) throw new Error("HTTP " + res.status);
      const data = await res.json();
      return {
        lat: data.lat || 35.681236,   // 기본 도쿄
        lon: data.lon || 139.767125,
        loggedIn: data.loggedIn || false
      };
    } catch (e) {
      console.warn("[Shelter] 사용자 위치 가져오기 실패, 기본값 사용", e);
      return { lat: 35.681236, lon: 139.767125, loggedIn: false };
    }
  }

  // ========================
  // 거리계산 (하버사인)
  // ========================
  function distKm(lat1, lon1, lat2, lon2) {
    const R = 6371;
    const dLat = (lat2 - lat1) * Math.PI / 180;
    const dLon = (lon2 - lon1) * Math.PI / 180;
    const a =
      Math.sin(dLat / 2) ** 2 +
      Math.cos(lat1 * Math.PI / 180) *
      Math.cos(lat2 * Math.PI / 180) *
      Math.sin(dLon / 2) ** 2;
    return R * 2 * Math.asin(Math.sqrt(a));
  }

  async function enable(mapInstance) {
      map = mapInstance;
      await loadShelters();
      await loadEmergencyShelters();
      // 생성된 shelterMarkers, emergencyMarkers를 map에 붙이기
      [...shelterMarkers, ...emergencyMarkers].forEach(m => m.setMap(map));
    }
	
  async function enable(mapInstance) {
    map = mapInstance;

    // 1) 사용자 위치 불러오기 (로그인 여부에 따라 다르게)
    const user = await getUserLocation();
    const cx = user.lat;
    const cy = user.lon;

    // 2) shelters.csv 읽기
    const res1 = await fetch("/csv/shelters/shelters.csv");
    const text1 = await res1.text();
    const data1 = parseCSV(text1);

    // 3) emergency.csv 읽기
    const res2 = await fetch("/csv/shelters/emergency.csv");
    const text2 = await res2.text();
    const data2 = parseCSV(text2);

    // 4) 두 데이터 합치기
    const all = [
      ...data1.map(d => ({ ...d, type: "指定避難所" })),
      ...data2.map(d => ({ ...d, type: "指定緊急避難場所" }))
    ];

	// 👉 공통 함수로 뽑아내기
	  function renderNearest(cx, cy) {
	    // 기존 마커 제거
	    [...shelterMarkers, ...emergencyMarkers].forEach(m => m.setMap(null));
	    shelterMarkers = [];
	    emergencyMarkers = [];

	    // 가까운 30개만 다시 그림
	    const sorted = all
	      .map(item => {
	        const lat = parseFloat(item["緯度"]);
	        const lng = parseFloat(item["経度"]);
	        return (!lat || !lng) ? null : {
	          ...item,
	          lat, lng,
	          dist: distKm(cx, cy, lat, lng)
	        };
	      })
	      .filter(Boolean)
	      .sort((a, b) => a.dist - b.dist)
	      .slice(0, 30);

	    sorted.forEach(item => {
	      const marker = new google.maps.Marker({
	        position: { lat: item.lat, lng: item.lng },
	        map,
	        title: item["名称"] || item["施設名"] || item.type,
	        icon: {
	          url: item.type === "指定避難所"
	            ? "http://maps.google.com/mapfiles/ms/icons/blue-dot.png"
	            : "http://maps.google.com/mapfiles/ms/icons/red-dot.png"
	        }
	      });

	      const info = new google.maps.InfoWindow({
	        content: `
	          <div style="min-width:200px">
	            <b>${item["名称"] || item["施設名"]}</b><br>
	            ${item["住所"] || ""}<br>
	            種別: ${item.type}
	          </div>
	        `
	      });

	      marker.addListener("click", () => info.open(map, marker));

	      if (item.type === "指定避難所") shelterMarkers.push(marker);
	      else emergencyMarkers.push(marker);
	    });
	  }

	  // 3) 최초 실행 (사용자 좌표 기준)
	  renderNearest(cx, cy);
	  map.setCenter({ lat: cx, lng: cy });

	  // 4) idle 이벤트 등록 (지도 이동 시 30개 갱신)
	  map.addListener("idle", () => {
	    const c = map.getCenter();
	    renderNearest(c.lat(), c.lng());
	  });
	}

  function disable() {
    [...shelterMarkers, ...emergencyMarkers].forEach(m => m.setMap(null));
    shelterMarkers = [];
    emergencyMarkers = [];
  }

  window.ShelterFeature = { enable, disable };
})();
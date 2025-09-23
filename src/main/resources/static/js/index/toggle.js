let map;

// 사용자 위치 가져오기 함수
async function getUserLocation() {
    try {
        const res = await fetch("/api/me/location", { cache: "no-store" });
        if (!res.ok) throw new Error("HTTP " + res.status);
        const data = await res.json();
        console.log("사용자 위치 API 응답:", data);
        return {
            lat: (typeof data.lat === "number") ? data.lat : 35.681236,   // 도쿄 기본
            lon: (typeof data.lon === "number") ? data.lon : 139.767125,
            loggedIn: !!data.loggedIn
        };
    } catch (e) {
        console.warn("[InitMap] 사용자 위치 가져오기 실패, 기본값 사용", e);
        return { lat: 35.681236, lon: 139.767125, loggedIn: false };
    }
}

// 구글맵 초기화 (사용자 위치 기반)
async function initMap() {
    const mapElement = document.getElementById("map");
    
    // 사용자 위치를 API로 가져오기
    const userLocation = await getUserLocation();
    const center = { lat: userLocation.lat, lng: userLocation.lon };
    
    console.log("지도 초기화 - 중심좌표:", center, "로그인:", userLocation.loggedIn);
    
    map = new google.maps.Map(mapElement, {
        center: center,
        zoom: 12
    });
    
    // 다른 스크립트에서 공용으로 쓰도록
    window._map = map;
    window.map = map;
    
    // 로그인한 사용자라면 해당 위치에 핀 표시
    if (userLocation.loggedIn) {
        new google.maps.Marker({
            position: center,
            map: map,
            title: "내 위치",
            icon: {
                url: "https://maps.google.com/mapfiles/ms/icons/red-dot.png",
                scaledSize: new google.maps.Size(32, 32)
            }
        });
    }
}

// 토글 이벤트 바인딩
document.addEventListener("DOMContentLoaded", () => {
    const weatherToggle = document.getElementById('weatherToggle');
    const disasterToggle = document.getElementById('disasterToggle');
    const shelterToggle = document.getElementById('shelterToggle');
    const hospitalToggle = document.getElementById('hospitalToggle');

    // 날씨 토글
    if (weatherToggle) {
        weatherToggle.addEventListener('change', () => {
            if (weatherToggle.checked) {
                WeatherFeature.enable(window._map);
            } else {
                WeatherFeature.disable();
            }
        });
    }

    // 재난 토글
    if (disasterToggle) {
        disasterToggle.addEventListener('change', () => {
            if (disasterToggle.checked) {
                DisasterFeature.enable(window._map);
            } else {
                DisasterFeature.disable();
            }
        });
    }

    // 대피소 토글
    if (shelterToggle) {
        shelterToggle.addEventListener('change', () => {
            if (shelterToggle.checked) {
                ShelterFeature.enable(window._map);
                window._map.setZoom(15);
            } else {
                ShelterFeature.disable();
                window._map.setZoom(12);
            }
        });
    }

    // 병원 토글
    if (hospitalToggle) {
        hospitalToggle.addEventListener('change', () => {
            if (hospitalToggle.checked) {
                HospitalOSMFeature.enable(window._map);
                window._map.setZoom(15);
            } else {
                HospitalOSMFeature.disable();
                window._map.setZoom(12);
            }
        });
    }
});
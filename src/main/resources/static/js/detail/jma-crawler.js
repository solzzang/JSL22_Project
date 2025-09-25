// JMA(일본 기상청) 재해 정보 크롤러
class JMADisasterCrawler {
    constructor() {
        this.proxyUrl = 'https://api.allorigins.win/get?url=';
        this.baseUrl = 'https://www.jma.go.jp';
    }

    // CORS 문제 해결을 위한 프록시 요청
    async fetchWithProxy(url) {
        try {
            const response = await fetch(this.proxyUrl + encodeURIComponent(url));
            const data = await response.json();
            return data.contents;
        } catch (error) {
            console.error('프록시 요청 실패:', error);
            throw error;
        }
    }

    // 지진 정보 가져오기 (테스트용)
    async getEarthquakeData() {
        console.log('지진 정보 수집 중...');
        
        try {
            // 실제로는 JMA 사이트에서 가져오지만, 우선 테스트 데이터
            const testData = [
                {
                    magnitude: '5.2',
                    location: '도쿄 근처',
                    time: new Date().toLocaleString(),
                    depth: '10km',
                    type: 'earthquake'
                }
            ];
            
            console.log(`지진 데이터 ${testData.length}개 수집 완료`);
            return testData;
            
        } catch (error) {
            console.error('지진 데이터 수집 실패:', error);
            return [];
        }
    }

    // 화산 정보 가져오기 (테스트용)
    async getVolcanoData() {
        console.log('화산 정보 수집 중...');
        
        const testData = [
            {
                name: '후지산',
                alertLevel: '1',
                status: '정상',
                time: new Date().toLocaleString(),
                type: 'volcano'
            }
        ];
        
        console.log(`화산 데이터 ${testData.length}개 수집 완료`);
        return testData;
    }

    // 모든 재해 정보 수집
    async collectAllData() {
        console.log('=== JMA 재해 정보 크롤링 시작 ===');
        
        try {
            const [earthquakes, volcanoes] = await Promise.all([
                this.getEarthquakeData(),
                this.getVolcanoData()
            ]);

            const allData = {
                earthquakes: earthquakes,
                volcanoes: volcanoes,
                tsunamis: [], // 나중에 추가
                typhoons: [], // 나중에 추가
                lastUpdated: new Date().toISOString(),
                totalCount: earthquakes.length + volcanoes.length
            };

            console.log('=== 데이터 수집 완료 ===');
            console.log(`총 ${allData.totalCount}개 데이터 수집`);
            console.log('데이터:', allData);

            return allData;

        } catch (error) {
            console.error('데이터 수집 중 오류:', error);
            throw error;
        }
    }
}

// 크롤러 인스턴스 생성
const jmaCrawler = new JMADisasterCrawler();

// 테스트 실행 함수
async function testCrawler() {
    try {
        const data = await jmaCrawler.collectAllData();
        console.log('크롤링 테스트 성공!', data);
        return data;
    } catch (error) {
        console.error('크롤링 테스트 실패:', error);
    }
}

// 전역에서 사용할 수 있도록 export
window.jmaCrawler = jmaCrawler;
window.testCrawler = testCrawler;

console.log('JMA 크롤러 로드 완료');
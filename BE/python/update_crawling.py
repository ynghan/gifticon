import time
from typing import List
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager


class RestaurantCrawlingMenuDto:
    def __init__(self):
        self.menuName = None
        self.menuDesc = None
        self.menuPrice = None
        self.menuImage = None


class RestaurantCrawlingStoreDto:
    def __init__(self):
        self.placeName = None
        self.mainImageUrl = None
        self.addressName = None
        self.storeInfo = None
        self.menus: List[RestaurantCrawlingMenuDto] = []


def create_webdriver(headless: bool = True) -> webdriver.Chrome:
    chrome_options = Options()

    # ----------------------------
    # (A) Headless 모드에서 신형 옵션 사용
    #     Chrome 109+ 이상인 경우
    #     구버전이라면 --headless 로 되돌리세요
    # ----------------------------
    if headless:
        chrome_options.add_argument("--headless=new")
        
            # 이미지 로딩 차단 설정
    chrome_prefs = {
        "profile.default_content_setting_values": {
            "images": 2  # 2: 차단 / 1: 허용
        }
    }
    chrome_options.experimental_options["prefs"] = chrome_prefs

    # ----------------------------
    # (B) remote-allow-origins 추가 (Chrome 111+ 이슈)
    # ----------------------------
    # chrome_options.add_argument("--remote-allow-origins=*")

    # ----------------------------
    # (C) GPU 사용에 관련된 옵션들
    #     필요없는 것은 빼보고 테스트
    # ----------------------------
    chrome_options.add_argument("--disable-gpu")
    chrome_options.add_argument("--disable-dev-shm-usage")
    chrome_options.add_argument("--no-sandbox")
    chrome_options.add_argument('--disable-images')
    chrome_options.add_experimental_option("prefs", {'profile.managed_default_content_settings.images': 2})
    chrome_options.add_argument('--blink-settings=imagesEnabled=false')

    # (D) 일반적으로 사용하는 최적화/창크기 옵션
    chrome_options.add_argument("--window-size=1920,1080")
    chrome_options.page_load_strategy = "none"

    # (E) User-Agent를 명시적으로 지정 (선택)
    chrome_options.add_argument(
        "user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
        "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36"
    )

    driver = webdriver.Chrome(
        service=Service(ChromeDriverManager().install()),
        options=chrome_options
    )
    return driver


def wait_and_switch_to_frame(driver: webdriver.Chrome, frame_css: str, timeout: int = 10):
    """
    특정 iframe이 로드될 때까지 대기한 뒤 해당 iframe으로 전환.
    """
    WebDriverWait(driver, timeout).until(EC.frame_to_be_available_and_switch_to_it((By.CSS_SELECTOR, frame_css)))


def click_element_with_wait(driver: webdriver.Chrome, element, timeout: int = 5):
    """
    클릭 가능한 상태가 될 때까지 기다린 뒤 클릭. (element는 이미 찾은 요소)
    """
    WebDriverWait(driver, timeout).until(EC.element_to_be_clickable(element))
    driver.execute_script("arguments[0].click();", element)


def extract_store_info(driver: webdriver.Chrome, wait: WebDriverWait, dto: RestaurantCrawlingStoreDto):
    """
    상세페이지(entryIframe)에서 가게명, 대표이미지, 주소, '정보' 탭 내용을 파싱하여 dto에 저장.
    """
    # 가게명
    try:
        name_el = wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, "span.GHAhO")))
        dto.placeName = name_el.text.strip()
    except NoSuchElementException:
        dto.placeName = "(가게명 없음)"

    # 대표 이미지
    try:
        main_img = driver.find_element(By.CSS_SELECTOR, ".fNygA>a>img").get_attribute("src")
        dto.mainImageUrl = main_img
    except NoSuchElementException:
        dto.mainImageUrl = "(이미지 없음)"

    # 주소
    try:
        address_el = driver.find_element(By.CSS_SELECTOR, "span.LDgIH")
        dto.addressName = address_el.text.strip()
    except NoSuchElementException:
        dto.addressName = "(주소 없음)"

    # "정보" 탭 (상단 탭 목록)
    info_tabs = driver.find_elements(By.CSS_SELECTOR, ".flicking-camera > a")
    info_tab = next((it for it in info_tabs if it.text.strip() == "정보"), None)
    if info_tab:
        click_element_with_wait(driver, info_tab)
        # info_tab 선택될 때까지 대기
        WebDriverWait(driver, 3).until(lambda _: info_tab.get_attribute("aria-selected") == "true")
        try:
            info_el = driver.find_element(By.CSS_SELECTOR, "div.T8RFa")
            dto.storeInfo = info_el.text.strip()
        except NoSuchElementException:
            dto.storeInfo = "(매장 소개 없음)"
    else:
        dto.storeInfo = "(정보 탭이 없습니다)"


def extract_menu_info(driver: webdriver.Chrome, wait: WebDriverWait, dto: RestaurantCrawlingStoreDto):
    """
    상세페이지(entryIframe)에서 '메뉴' 탭 클릭 후 메뉴 정보(이름, 소개, 가격, 이미지)를 크롤링하여 dto에 저장.
    """
    menu_tabs = driver.find_elements(By.CSS_SELECTOR, ".flicking-camera > a")
    menu_tab = next((it for it in menu_tabs if it.text.strip() == "메뉴"), None)
    if not menu_tab:
        print("메뉴 탭이 존재하지 않습니다.")
        return

    # 메뉴 탭 클릭
    click_element_with_wait(driver, menu_tab)
    WebDriverWait(driver, 3).until(lambda _: menu_tab.get_attribute("aria-selected") == "true")

    try:
        # menu_elements = driver.find_elements(By.CSS_SELECTOR, "li.E2jtL") # 기존
        # 실제 DOM 검사 후 아래 셀렉터도 변경 가능
        menu_elements = driver.find_elements(By.CSS_SELECTOR, "li.E2jtL")
        menu_list: List[RestaurantCrawlingMenuDto] = []

        for menu_el in menu_elements:
            menu_dto = RestaurantCrawlingMenuDto()

            # 메뉴 이름
            try:
                name_el2 = menu_el.find_element(By.CSS_SELECTOR, "span.lPzHi")
                menu_dto.menuName = name_el2.text.strip()
            except NoSuchElementException:
                menu_dto.menuName = "(메뉴명 없음)"

            # 메뉴 소개
            try:
                desc_el = menu_el.find_element(By.CSS_SELECTOR, "div.kPogF")
                menu_dto.menuDesc = desc_el.text.strip()
            except NoSuchElementException:
                menu_dto.menuDesc = "(소개 없음)"

            # 메뉴 가격
            try:
                price_el = menu_el.find_element(By.CSS_SELECTOR, "div.GXS1X")
                price_str = price_el.get_attribute("textContent") or "(가격정보 없음)"
                menu_dto.menuPrice = price_str.strip()
            except NoSuchElementException:
                menu_dto.menuPrice = "(가격 없음)"

            # 메뉴 이미지
            try:
                img_el = menu_el.find_element(By.CSS_SELECTOR, "div.place_thumb img")
                # Lazy 로딩일 경우 get_attribute('src')가 비어있을 수도 있으므로 data-src 확인
                img_src = img_el.get_attribute("src") or img_el.get_attribute("data-src")
                menu_dto.menuImage = img_src if img_src else "(이미지 없음)"
            except NoSuchElementException:
                menu_dto.menuImage = "(이미지 없음)"

            menu_list.append(menu_dto)

        dto.menus = menu_list

    except NoSuchElementException:
        print("메뉴 탭은 있으나, 메뉴 정보가 없거나 구조가 다릅니다.")


def crawl_store_details_by_address(address: str) -> List[RestaurantCrawlingStoreDto]:
    """
    1) 네이버 지도 접속 후, address로 검색
    2) 검색결과 첫 매장(들)을 클릭해 상세정보/메뉴 크롤링
    3) RestaurantCrawlingStoreDto 리스트 반환
    """
    result_list: List[RestaurantCrawlingStoreDto] = []
    driver = create_webdriver(headless=True)  # 필요 시 headless=True

    try:
        driver.get("https://map.naver.com/")
        wait = WebDriverWait(driver, 10)

        # 검색창 로딩 대기 후 검색어 입력
        search_box = wait.until(
            EC.presence_of_element_located((By.CSS_SELECTOR, "input.input_search"))
        )
        search_box.clear()
        search_box.send_keys(address)
        search_box.send_keys(Keys.ENTER)

        # 검색결과 iframe으로 전환
        wait_and_switch_to_frame(driver, "iframe#searchIframe", timeout=10)

        # 검색 결과 목록 로딩 대기
        shop_links = wait.until(
            EC.presence_of_all_elements_located(
                (By.CSS_SELECTOR, "div>.place_bluelink>.TYaxT, div.place_bluelink>span.YwYLL")
            )
        )

        # 최대 1개(혹은 2개)만 가져온다고 가정
        max_count = min(1, len(shop_links))
        for i in range(max_count):
            shop = shop_links[i]
            # 상세페이지 이동
            click_element_with_wait(driver, shop, timeout=3)

            # 상세페이지 URL이 "/place/"를 포함할 때까지 대기
            short_wait = WebDriverWait(driver, 3)
            short_wait.until(lambda d: "/place/" in d.current_url)

            # 메인 문서로 돌아온 뒤 entryIframe에 다시 진입
            driver.switch_to.default_content()
            wait_and_switch_to_frame(driver, "iframe#entryIframe", timeout=10)

            dto = RestaurantCrawlingStoreDto()
            # (1) 정보 추출
            extract_store_info(driver, wait, dto)
            # (2) 메뉴 추출
            extract_menu_info(driver, wait, dto)

            # 결과 리스트에 추가
            result_list.append(dto)

            # 다시 검색 결과 iframe로 복귀 (다음 매장 크롤링용)
            driver.switch_to.default_content()
            wait_and_switch_to_frame(driver, "iframe#searchIframe", timeout=10)

    except Exception as e:
        print("[ERROR]", e)
    finally:
        driver.quit()

    return result_list


if __name__ == "__main__":
    keyword = "하단끝집"
    start_time = time.time()

    results = crawl_store_details_by_address(keyword)
    for idx, store in enumerate(results, start=1):
        print(f"\n[{idx}] 매장명: {store.placeName}")
        print(f"    주소: {store.addressName}")
        print(f"    대표이미지: {store.mainImageUrl}")
        print(f"    매장 소개: {store.storeInfo}")
        for m_idx, menu in enumerate(store.menus, start=1):
            print(f"       (메뉴#{m_idx}) {menu.menuName} / {menu.menuDesc} / {menu.menuPrice} / {menu.menuImage}")

    end_time = time.time()
    print(f"\n크롤링에 걸린 시간: {end_time - start_time:.2f}초")
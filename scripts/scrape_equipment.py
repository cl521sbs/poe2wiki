#!/usr/bin/env python3
"""从 poe2db.tw 抓取装备数据并导入 PostgreSQL"""

import json
import re
import time
import psycopg2
import requests
from bs4 import BeautifulSoup

BASE_URL = "https://poe2db.tw"
DB_CONFIG = {
    "host": "localhost",
    "port": 5432,
    "user": "postgres",
    "password": "postgres",
    "dbname": "poe2wiki",
}


def fetch_page(url):
    resp = requests.get(url, timeout=30)
    resp.raise_for_status()
    time.sleep(0.5)
    return BeautifulSoup(resp.text, "html.parser")


def parse_equipment_from_detail(url, name_cn, name_en, category):
    soup = fetch_page(url)
    data = {
        "name_cn": name_cn,
        "name_en": name_en,
        "category": category,
        "rarity": "unique",
        "req_level": 1,
        "req_str": 0,
        "req_dex": 0,
        "req_int": 0,
        "armour": 0,
        "evasion": 0,
        "energy_shield": 0,
        "effect_cn": "",
        "effect_en": "",
        "flavor_text_cn": "",
        "flavor_text_en": "",
        "icon_url": "",
    }

    tables = soup.select("table")
    for table in tables:
        rows = table.select("tr")
        for row in rows:
            cells = row.select("td, th")
            if len(cells) >= 2:
                key = cells[0].get_text(strip=True)
                value = cells[1].get_text(strip=True)
                _map_equip_field(data, key, value)

    return data


def _map_equip_field(data, key, value):
    mapping = {
        "需求等级": ("req_level", int),
        "需求力量": ("req_str", int),
        "需求敏捷": ("req_dex", int),
        "需求智力": ("req_int", int),
        "护甲": ("armour", int),
        "闪避": ("evasion", int),
        "能量护盾": ("energy_shield", int),
    }
    for keyword, (field, converter) in mapping.items():
        if keyword in key:
            try:
                nums = re.findall(r"\d+", value)
                if nums:
                    data[field] = converter(nums[0])
            except (ValueError, IndexError):
                pass


def save_to_db(conn, equipment_list):
    with conn.cursor() as cur:
        for eq in equipment_list:
            cur.execute(
                """INSERT INTO equipment (name_cn, name_en, category, rarity,
                   req_level, req_str, req_dex, req_int, armour, evasion,
                   energy_shield, effect_cn, effect_en, flavor_text_cn,
                   flavor_text_en, icon_url)
                   VALUES (%(name_cn)s, %(name_en)s, %(category)s, %(rarity)s,
                   %(req_level)s, %(req_str)s, %(req_dex)s, %(req_int)s,
                   %(armour)s, %(evasion)s, %(energy_shield)s, %(effect_cn)s,
                   %(effect_en)s, %(flavor_text_cn)s, %(flavor_text_en)s,
                   %(icon_url)s)""",
                eq,
            )
    conn.commit()


def main():
    print("开始抓取装备数据...")
    soup = fetch_page(f"{BASE_URL}/tw/Unique_item")
    item_links = []
    for link in soup.select("a[href]"):
        href = link.get("href", "")
        if "/tw/" in href and link.get_text(strip=True):
            item_links.append((link.get_text(strip=True), BASE_URL + href))

    print(f"找到 {len(item_links)} 个装备链接")

    conn = psycopg2.connect(**DB_CONFIG)
    items = []

    for name_cn, url in item_links[:10]:
        print(f"  抓取: {name_cn}")
        try:
            item_data = parse_equipment_from_detail(url, name_cn, name_cn, "armour")
            items.append(item_data)
        except Exception as e:
            print(f"  错误: {e}")

    save_to_db(conn, items)
    print(f"成功导入 {len(items)} 条装备数据")
    conn.close()


if __name__ == "__main__":
    main()

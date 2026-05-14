#!/usr/bin/env python3
"""从 poe2db.tw 抓取技能数据并导入 PostgreSQL"""

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
    """获取页面并解析为 BeautifulSoup"""
    resp = requests.get(url, timeout=30)
    resp.raise_for_status()
    time.sleep(0.5)  # 礼貌等待
    return BeautifulSoup(resp.text, "html.parser")


def parse_skill_from_detail(url, name_cn, name_en, skill_type):
    """从技能详情页解析属性"""
    soup = fetch_page(url)
    data = {
        "name_cn": name_cn,
        "name_en": name_en,
        "type": skill_type,
        "tags": [],
        "level": 1,
        "attr_requirements": {},
        "mana_cost": 0,
        "cooldown": 0,
        "cast_time": 0,
        "damage_multiplier": 100,
        "damage_type": "physical",
        "effect_cn": "",
        "effect_en": "",
        "icon_url": "",
    }

    # 解析详情表格（具体选择器需根据实际 HTML 调整）
    tables = soup.select("table")
    for table in tables:
        rows = table.select("tr")
        for row in rows:
            cells = row.select("td, th")
            if len(cells) >= 2:
                key = cells[0].get_text(strip=True)
                value = cells[1].get_text(strip=True)
                _map_skill_field(data, key, value)

    # 抓取技能效果描述
    desc_div = soup.select_one("div.description, div.skill-desc")
    if desc_div:
        data["effect_cn"] = desc_div.get_text(strip=True)

    return data


def _map_skill_field(data, key, value):
    """将网页表格的 key 映射到数据库字段"""
    mapping = {
        "魔力消耗": ("mana_cost", int),
        "冷却时间": ("cooldown", float),
        "施放时间": ("cast_time", float),
        "伤害倍率": ("damage_multiplier", float),
        "伤害类型": ("damage_type", str),
    }
    for keyword, (field, converter) in mapping.items():
        if keyword in key:
            try:
                nums = re.findall(r"[\d.]+", value)
                if nums:
                    data[field] = converter(nums[0])
            except (ValueError, IndexError):
                pass


def save_to_db(conn, skills):
    """批量插入技能数据"""
    with conn.cursor() as cur:
        for skill in skills:
            cur.execute(
                """INSERT INTO skills (name_cn, name_en, type, tags, level,
                   attr_requirements, mana_cost, cooldown, cast_time,
                   damage_multiplier, damage_type, effect_cn, effect_en, icon_url)
                   VALUES (%(name_cn)s, %(name_en)s, %(type)s, %(tags)s, %(level)s,
                   %(attr_requirements)s, %(mana_cost)s, %(cooldown)s, %(cast_time)s,
                   %(damage_multiplier)s, %(damage_type)s, %(effect_cn)s, %(effect_en)s, %(icon_url)s)""",
                {
                    **skill,
                    "tags": json.dumps(skill["tags"]),
                    "attr_requirements": json.dumps(skill["attr_requirements"]),
                },
            )
    conn.commit()


def main():
    print("开始抓取技能数据...")
    # 从技能列表页获取所有技能链接
    soup = fetch_page(f"{BASE_URL}/tw/Skill_gems")
    skill_links = []
    for link in soup.select("a[href]"):
        href = link.get("href", "")
        if "/tw/" in href and link.get_text(strip=True):
            skill_links.append((link.get_text(strip=True), BASE_URL + href))

    print(f"找到 {len(skill_links)} 个技能链接")

    conn = psycopg2.connect(**DB_CONFIG)
    skills = []

    for name_cn, url in skill_links[:10]:  # 先抓取前10个作为 MVP
        print(f"  抓取: {name_cn}")
        try:
            skill_data = parse_skill_from_detail(url, name_cn, name_cn, "active")
            skills.append(skill_data)
        except Exception as e:
            print(f"  错误: {e}")

    save_to_db(conn, skills)
    print(f"成功导入 {len(skills)} 条技能数据")
    conn.close()


if __name__ == "__main__":
    main()

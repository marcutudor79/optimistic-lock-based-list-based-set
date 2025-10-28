#!/usr/bin/env python3
"""
parse_and_plot.py

Parse the benchmark output produced by `test-script.sh` and generate 7 PNG graphs:

- For each algorithm (3 graphs): throughput vs threads, 3 lines = different list sizes (100,1000,10000)
- For each algorithm (3 graphs): throughput vs threads, 3 lines = different update ratios (0,10,100) for list size 100
- One comparison graph: throughput vs threads where each line is a different algorithm (fixed update ratio=10, list size=1000)

Usage:
    python3 parse_and_plot.py /path/to/output.log --outdir graphs --show

Dependencies:
    pip install pandas matplotlib seaborn

"""
import re
import sys
import argparse
from pathlib import Path
import math

import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

sns.set(style="whitegrid")

# regex patterns
RE_RUNNING = re.compile(r"^\s*→\s*Running:\s*(?P<alg>\S+)(?:.*?update\s*ratio\s*(?P<u>\d+))?(?:.*?list\s*size\s*(?P<i>\d+))?", re.IGNORECASE)
RE_LISTSIZE = re.compile(r"^\s*list[- ]size:\s*(?P<list>\d+)", re.IGNORECASE)
RE_UPDRATIO = re.compile(r"^\s*update\s*ratio:\s*(?P<upd>\d+)", re.IGNORECASE)
RE_THREADS = re.compile(r"^\s*threads:\s*(?P<th>\d+)", re.IGNORECASE)
RE_THROUGHPUT = re.compile(r"Throughput\s*\(ops/s\)\s*:\s*(?P<val>[-+0-9.Ee]+)")


def parse_log(path):
    rows = []
    current = dict(algorithm=None, list_size=None, update_ratio=None)
    last_threads = None

    with open(path, 'r', encoding='utf-8') as f:
        for raw in f:
            line = raw.rstrip()
            if not line:
                continue

            m = RE_RUNNING.search(line)
            if m:
                alg = m.group('alg')
                current['algorithm'] = alg
                # if the same line contains numbers parse them
                if m.group('u'):
                    current['update_ratio'] = int(m.group('u'))
                if m.group('i'):
                    current['list_size'] = int(m.group('i'))
                last_threads = None
                continue

            m = RE_LISTSIZE.search(line)
            if m:
                current['list_size'] = int(m.group('list'))
                continue

            m = RE_UPDRATIO.search(line)
            if m:
                current['update_ratio'] = int(m.group('upd'))
                continue

            m = RE_THREADS.search(line)
            if m:
                last_threads = int(m.group('th'))
                continue

            m = RE_THROUGHPUT.search(line)
            if m and last_threads is not None and current['algorithm'] is not None:
                # parse throughput value, handle scientific E notation
                s = m.group('val')
                try:
                    val = float(s)
                except ValueError:
                    # handle Java-style 1.2140990024937656E7
                    val = float(s.replace('E', 'e'))
                rows.append({
                    'algorithm': current['algorithm'],
                    'list_size': current['list_size'],
                    'update_ratio': current['update_ratio'],
                    'threads': last_threads,
                    'throughput': val,
                })
                last_threads = None
                continue

    df = pd.DataFrame(rows)
    # sanitize / fill defaults
    if 'list_size' in df.columns:
        df['list_size'] = df['list_size'].astype('Int64')
    if 'update_ratio' in df.columns:
        df['update_ratio'] = df['update_ratio'].astype('Int64')
    return df


def plot_lines(df, outdir, title, x, hue, palette=None, xlabel=None, ylabel='Throughput (ops/s)', logy=False):
    plt.figure(figsize=(8,6))
    ax = sns.lineplot(data=df, x=x, y='throughput', hue=hue, marker='o', palette=palette)
    ax.set_title(title)
    ax.set_xlabel(xlabel or x)
    ax.set_ylabel(ylabel)
    if logy:
        ax.set_yscale('log')
    ax.legend(title=hue)
    plt.tight_layout()
    # safe filename
    fname = re.sub(r"[^0-9a-zA-Z_\-]+", '_', title) + '.png'
    path = outdir / fname
    plt.savefig(path)
    print('Wrote', path)
    plt.close()


def main():
    p = argparse.ArgumentParser(description='Parse benchmark log and plot graphs')
    p.add_argument('logfile', nargs='?', default='-', help='Log file to parse (or - for stdin)')
    p.add_argument('--outdir', '-o', default='graphs', help='Output directory for PNGs')
    p.add_argument('--show', action='store_true', help='Show plots interactively (matplotlib)')
    args = p.parse_args()

    if args.logfile == '-':
        # read stdin into temp file
        text = sys.stdin.read()
        tmp = Path('tmp_bench_log.txt')
        tmp.write_text(text, encoding='utf-8')
        infile = tmp
    else:
        infile = Path(args.logfile)
        if not infile.exists():
            print('Log file not found:', infile, file=sys.stderr)
            sys.exit(1)

    outdir = Path(args.outdir)
    outdir.mkdir(parents=True, exist_ok=True)

    df = parse_log(infile)
    if df.empty:
        print('No data parsed from', infile)
        sys.exit(1)

    # Ensure numeric types
    df['threads'] = df['threads'].astype(int)
    df['throughput'] = df['throughput'].astype(float)

    algorithms = sorted(df['algorithm'].unique())
    print('Algorithms found:', algorithms)

    # 1) First three graphs: for each algorithm, fixed update ratio 10, varying list sizes
    for alg in algorithms:
        sub = df[(df['algorithm'] == alg) & (df['update_ratio'] == 10) & df['list_size'].notna()]
        if sub.empty:
            print('Skipping (no data) alg', alg, 'for update_ratio=10')
            continue
        title = f"{alg} — throughput vs threads (update_ratio=10) — varying list_size"
        # convert list_size to string for hue
        sub = sub.copy()
        sub['list_size_str'] = sub['list_size'].astype(str)
        plot_lines(sub, outdir, title, x='threads', hue='list_size_str')

    # 2) Next three graphs: for each algorithm, fixed list_size=100, varying update ratios
    for alg in algorithms:
        sub = df[(df['algorithm'] == alg) & (df['list_size'] == 100) & df['update_ratio'].notna()]
        if sub.empty:
            print('Skipping (no data) alg', alg, 'for list_size=100')
            continue
        title = f"{alg} — throughput vs threads (list_size=100) — varying update_ratio"
        sub = sub.copy()
        sub['update_ratio_str'] = sub['update_ratio'].astype(str)
        plot_lines(sub, outdir, title, x='threads', hue='update_ratio_str')

    # 3) Final graph: throughput vs threads where each line is a different algorithm.
    # Use the fixed block: update_ratio=10 and list_size=1000 (third block in your log)
    comp = df[(df['update_ratio'] == 10) & (df['list_size'] == 1000)]
    if comp.empty:
        # fallback: pick any rows with list_size==1000 and update_ratio==10-like
        comp = df[df['list_size'] == 1000]
    if comp.empty:
        print('No data available for comparison graph (list_size=1000/update_ratio=10)')
    else:
        comp = comp.copy()
        plot_lines(comp, outdir, 'Algorithms comparison — throughput vs threads (list_size=1000, update_ratio=10)', x='threads', hue='algorithm')

    print('All done. PNGs in', outdir)
    if args.show:
        plt.show()


if __name__ == '__main__':
    main()

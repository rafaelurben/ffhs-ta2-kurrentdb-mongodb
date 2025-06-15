from pathlib import Path

import matplotlib.pyplot as plt
import pandas as pd

DATA_DIR = Path(__file__).parent / 'data'
RESULTS_DIR = Path(__file__).parent / 'results'

PAIR_NAMES = [
    ('ManyParents - Implementation 1 (MongoDB).csv', 'ManyParents - Implementation 2 (KurrentDB).csv'),
    ('ManyChildren - Implementation 1 (MongoDB).csv', 'ManyChildren - Implementation 2 (KurrentDB).csv'),
    ('ManyRestores - Implementation 1 (MongoDB).csv', 'ManyRestores - Implementation 2 (KurrentDB).csv')
]


def ns_to_ms(df):
    # Convert all columns with 'ns' in the header from ns to ms
    for col in df.columns:
        if '(ns)' in col:
            df[col] = df[col] / 1_000_000
            df.rename(columns={col: col.replace('(ns)', '(ms)')}, inplace=True)
    return df


def smooth_outliers(series, threshold=1.0):
    """
    Smooth outliers in a pandas Series using a rolling median and threshold.
    Outliers are replaced with the rolling median if they deviate by more than threshold * std.
    """
    window_size = max(1, min(50, len(series) // 10))  # Use a window size of 1 <= 10% of the series length <= 50
    rolling_median = series.rolling(window_size, center=True, min_periods=1).median()
    rolling_std = series.rolling(window_size, center=True, min_periods=1).std().fillna(0)
    diff = (series - rolling_median).abs()
    mask = diff > (threshold * rolling_std)
    smoothed = series.copy()
    smoothed[mask] = rolling_median[mask]
    return smoothed


def plot_pair(filepath1: Path, filepath2: Path, result_path: Path, test_name: str):
    df1 = pd.read_csv(filepath1)
    df2 = pd.read_csv(filepath2)
    df1 = ns_to_ms(df1)
    df2 = ns_to_ms(df2)

    fig, axs = plt.subplots(2, 2, figsize=(14, 7), gridspec_kw={'height_ratios': [5, 1]}, sharey="row", sharex=True)
    fig.suptitle(f'Comparison of "{test_name}" test runs', fontsize=14)
    # for every dataframe, create a subplot in the first row for durations and second row for event counts
    for dfx, (df, filepath) in enumerate(zip([df1, df2], [filepath1, filepath2])):
        main_plot_axes: plt.Axes = axs[0, dfx]
        main_plot_axes.set_title(filepath.name.split("- ")[1].replace('.csv', ''))
        main_plot_axes.set_xlabel('Event number')
        main_plot_axes.set_ylabel('Duration (ms)')

        count_plot_axes: plt.Axes = axs[1, dfx]
        count_plot_axes.set_xlabel('Event number')
        count_plot_axes.set_ylabel('Event count')

        for col in df1.columns:
            if '(ms)' in col:
                smoothed = smooth_outliers(df[col])
                main_plot_axes.plot(df.index, smoothed, label=col + ' (smoothed)')
            if '(events)' in col:
                count_plot_axes.plot(df.index, df[col], label=col, color='red')

        main_plot_axes.legend(loc='upper left')
        count_plot_axes.legend(loc='upper left')

    plt.tight_layout()
    plt.savefig(result_path)
    plt.close()


def main():
    RESULTS_DIR.mkdir(exist_ok=True)
    for file1, file2 in PAIR_NAMES:
        test_name = file1.split(' - ')[0].replace(' ', '')  # e.g. ManyParents

        filepath1 = DATA_DIR / file1
        filepath2 = DATA_DIR / file2
        result_path = RESULTS_DIR / f'{test_name}_comparison.png'

        plot_pair(filepath1, filepath2, result_path, test_name)

    print(f'Plots saved to {RESULTS_DIR}')


if __name__ == '__main__':
    main()

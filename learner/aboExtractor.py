import os
import json
import numpy as np
from matplotlib import pyplot as plt
from sklearn.model_selection import cross_val_score
from sklearn.neural_network import MLPClassifier

min_minute = 0
max_minute = 61
min_samples = 200
num_hidden_nodes = (5, )
solver = 'sgd'
sample_set = dict()
classifiers = dict()
scores = []
mean_scores = []
std_deviations = []


def load_data():
    minute = min_minute
    while minute < max_minute:
        print ("Loading data for minute - %d" % minute)
        samples = get_samples(minute)
        if samples['num_samples'] < min_samples:
            break
        sample_set[minute] = samples
        minute += 1


def get_samples(minute):
    x = []
    y = []
    json_list = read_all_json(minute)
    for json_obj in json_list:
        x_i = read_features(json_obj)
        y_i = read_classes(json_obj)
        for X_i_j in x_i:
            x.append(X_i_j)
        for Y_i_j in y_i:
            y.append(Y_i_j)

    return dict(num_samples=len(x), X=np.asarray(x), Y=np.asarray(y))


def read_features(json_obj):
    x_i = []
    game_time = json_obj['gameTime']
    for hero_name in json_obj['state']:
        hero_info = json_obj['state'][hero_name]
        features = [game_time, hero_info['state']['kills'], hero_info['state']['teamKills'],
                    hero_info['state']['deaths'], hero_info['state']['teamDeaths'], hero_info['state']['netWorth'],
                    hero_info['state']['teamNetWorth'], hero_info['state']['xp'], hero_info['state']['teamXp'],
                    hero_info['state']['towersLeft'], hero_info['state']['enemyTowersLeft'], 1]

        x_i.append(np.asarray(features))
    return x_i


def read_classes(json_obj):
    y_i = []
    for hero_name in json_obj['state']:
        hero_info = json_obj['state'][hero_name]
        classes = np.asarray(hero_info['lastItemPurchased'])

        y_i.append(classes)
    return y_i


def read_all_json(minute):
    json_list = []
    root_dir = "extractedReplays"
    file_name = str(minute) + ".json"
    for dirName in os.listdir(root_dir):
        directories = os.path.join(root_dir, dirName, file_name)
        if os.path.isfile(directories):
            json_obj = json.load(open(directories))
            json_list.append(json_obj)

    return json_list


def init_mlp():
    for _ in sample_set:
        clf = MLPClassifier(hidden_layer_sizes=num_hidden_nodes, activation='logistic',
                            solver=solver, verbose=True,
                            max_iter=100, random_state=1)
        classifiers[_] = clf


def learn(minute):
    samples = sample_set[minute]
    classifier = classifiers[minute]
    # predicted = cross_val_predict(classifier, samples['X'], samples['Y'], cv=10)
    score = cross_val_score(classifier, samples['X'], samples['Y'], cv=10, scoring='accuracy')
    scores.append(score)
    print ("Score computed for minute - %d" % minute)


def calc_mean_scores():
    for score in scores:
        mean_scores.append(score.mean())
        std_deviations.append(score.std())


def plot_mean_scores():
    means = np.asarray(mean_scores)
    fig, ax = plt.subplots()
    ax.scatter(xrange(min_minute, max_minute), means)
    ax.set_xlabel("Game Time")
    ax.set_ylabel("Prediction rate")


def plot_std_deviations():
    std_devs = np.asarray(std_deviations)
    fig, ax = plt.subplots()
    ax.scatter(xrange(min_minute, max_minute), std_devs)
    ax.set_xlabel("Game Time")
    ax.set_ylabel("Standard deviations")


def main():
    load_data()
    print ("Data loaded")
    init_mlp()
    print ("Classifiers initialized")

    for minute in range(min_minute, max_minute):
        learn(minute)

    calc_mean_scores()
    plot_mean_scores()
    plot_std_deviations()
    plt.show()

main()

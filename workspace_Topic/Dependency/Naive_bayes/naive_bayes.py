import pickle
import nltk
import math

import numpy as np
from sklearn import linear_model
from sklearn import metrics
from sklearn.naive_bayes import MultinomialNB

path = "/Users/arzookatiyar/Desktop/workspace_Topic/Dependency/svm_format/";
writing_path = "/Users/arzookatiyar/Desktop/workspace_Topic/Dependency/Naive_bayes/";

features_index = [26140, 25848, 26286, 25298, 26265, 25812, 26692, 26269, 25519, 26007];

for i in range(0,9) :
    train_vector = [];
    train_labels = []
    with open(path+"training_holders_"+str(i), "r+") as f1 :
        for line in f1 :
            temp = np.array([0]*features_index[i]);
            elems = line.split("\t")
            for k in range(1, len(elems)) :
                index = elems[k].split(":")[0]
                val = elems[k].split(":")[1]
                #print index
                temp[int(index)-1] = val
            
            train_vector.append(temp)
            train_labels.append(elems[0])
        print "Done\t"+str(i)
        #pickle.dump(train_vector, open(writing_path+"training_holders_"+str(i), 'wb'))
        #pickle.dump(train_labels, open(writing_path+"training_holders_labels_"+str(i), 'wb'))
        print "Donewriting\t"+str(i)

    test_vector = []
    test_labels = []
    with open(path+"test_holders_"+str(i), "r+") as f2 :
        for line in f2 :
            temp = np.array([0]*features_index[i]);
            elems = line.split("\t")
            for k in range(1, len(elems)) :
                index = elems[k].split(":")[0]
                val = elems[k].split(":")[1]
                temp[int(index)-1] = val

            test_vector.append(temp)
            test_labels.append(elems[0])
        print "Done_test"
        #pickle.dump(test_vector, open(writing_path+"test_holders_"+str(i), 'wb'))
        #pickle.dump(test_labels, open(writing_path+"test_holders_labels_"+str(i), 'wb'))
    
    for i in range(0, 9) :
        #train_vector = pickle.load(open(writing_path+"training_holders_"+str(i), 'rb'))
        #train_labels = pickle.load(open(writing_path+"training_holders_labels_"+str(i), 'rb'))
        #test_vector = pickle.load(open(writing_path+"test_holders_"+str(i), 'rb'))
        #test_labels = pickle.load(open(writing_path+"test_holders_labels_"+str(i), 'rb'))

        X = np.array(train_vector)
        C = np.array(train_labels)

        clf = MultinomialNB()
        clf.fit(X, C)

        test_data = np.array(test_vector)
        result = clf.predict(test_data)
        print result

        correct = 0;
        incorrect = 0;

        correct_pos = 0;

        for index in range(0, len(result)) :
            if (test_labels[index] == result[index]) :
                correct = correct + 1
                if (test_labels[index] == '+1') :
                    correct_pos = correct_pos + 1
                
            else :
                incorrect = incorrect + 1
        print correct
        print incorrect
        print correct_pos

    

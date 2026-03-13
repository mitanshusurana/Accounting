import React, { useState } from 'react';
import { View, Text, TextInput, Button, StyleSheet, ScrollView, Alert } from 'react-native';

import { Platform } from 'react-native';

const API_URL = Platform.OS === 'android' ? 'http://10.0.2.2:8080/api' : 'http://localhost:8080/api';

export default function JournalEntryForm() {
  const [transactionDate, setTransactionDate] = useState('');
  const [voucherType, setVoucherType] = useState('Journal');
  const [narration, setNarration] = useState('');

  const [postings, setPostings] = useState([
    { accountId: '', debitAmount: '0', creditAmount: '0' },
    { accountId: '', debitAmount: '0', creditAmount: '0' }
  ]);

  const updatePosting = (index, field, value) => {
    const newPostings = [...postings];
    newPostings[index][field] = value;
    setPostings(newPostings);
  };

  const addPostingLine = () => {
    setPostings([...postings, { accountId: '', debitAmount: '0', creditAmount: '0' }]);
  };

  const handleSubmit = async () => {
    try {
      // Validate Basic input
      if (!transactionDate) {
        Alert.alert("Error", "Transaction date is required");
        return;
      }

      // Convert posting amounts to numbers for payload
      const formattedPostings = postings.map(p => ({
        accountId: p.accountId,
        debitAmount: parseFloat(p.debitAmount) || 0,
        creditAmount: parseFloat(p.creditAmount) || 0
      }));

      const payload = {
        transactionDate,
        voucherType,
        narration,
        postings: formattedPostings
      };

      const response = await fetch(`${API_URL}/journal-entries`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      });

      if (response.ok) {
        Alert.alert("Success", "Journal entry created successfully!");
        setTransactionDate('');
        setNarration('');
        setPostings([
          { accountId: '', debitAmount: '0', creditAmount: '0' },
          { accountId: '', debitAmount: '0', creditAmount: '0' }
        ]);
      } else {
        const errData = await response.json();
        Alert.alert("Error", `Failed to create entry: ${errData.message || response.status}`);
      }
    } catch (error) {
      Alert.alert("Error", `Network error: ${error.message}`);
    }
  };

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.header}>New Journal Entry</Text>

      <TextInput
        style={styles.input}
        placeholder="Transaction Date (YYYY-MM-DD)"
        value={transactionDate}
        onChangeText={setTransactionDate}
      />

      <TextInput
        style={styles.input}
        placeholder="Voucher Type"
        value={voucherType}
        onChangeText={setVoucherType}
      />

      <TextInput
        style={styles.input}
        placeholder="Narration"
        value={narration}
        onChangeText={setNarration}
        multiline
      />

      <Text style={styles.subHeader}>Postings</Text>
      {postings.map((posting, index) => (
        <View key={index} style={styles.postingContainer}>
          <Text style={styles.postingLabel}>Line {index + 1}</Text>
          <TextInput
            style={styles.input}
            placeholder="Account ID (e.g. Assets.Bank)"
            value={posting.accountId}
            onChangeText={(val) => updatePosting(index, 'accountId', val)}
          />
          <View style={styles.row}>
            <TextInput
              style={[styles.input, styles.halfInput]}
              placeholder="Debit Amount"
              keyboardType="numeric"
              value={posting.debitAmount}
              onChangeText={(val) => updatePosting(index, 'debitAmount', val)}
            />
            <TextInput
              style={[styles.input, styles.halfInput]}
              placeholder="Credit Amount"
              keyboardType="numeric"
              value={posting.creditAmount}
              onChangeText={(val) => updatePosting(index, 'creditAmount', val)}
            />
          </View>
        </View>
      ))}

      <View style={styles.buttonRow}>
        <Button title="Add Line" onPress={addPostingLine} color="#666" />
        <Button title="Submit Entry" onPress={handleSubmit} color="#007bff" />
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
    backgroundColor: '#fff',
  },
  header: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
    textAlign: 'center',
  },
  subHeader: {
    fontSize: 18,
    fontWeight: '600',
    marginTop: 20,
    marginBottom: 10,
  },
  input: {
    borderWidth: 1,
    borderColor: '#ccc',
    padding: 10,
    marginBottom: 10,
    borderRadius: 5,
  },
  postingContainer: {
    borderWidth: 1,
    borderColor: '#eee',
    padding: 10,
    marginBottom: 15,
    borderRadius: 5,
    backgroundColor: '#f9f9f9',
  },
  postingLabel: {
    fontWeight: 'bold',
    marginBottom: 5,
  },
  row: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  halfInput: {
    width: '48%',
  },
  buttonRow: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    marginTop: 20,
    marginBottom: 40,
  }
});

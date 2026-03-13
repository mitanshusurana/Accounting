import React from 'react';
import { SafeAreaView, StyleSheet, StatusBar } from 'react-native';
import JournalEntryForm from './screens/JournalEntryForm';

export default function App() {
  return (
    <SafeAreaView style={styles.container}>
      <StatusBar barStyle="dark-content" />
      <JournalEntryForm />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
});

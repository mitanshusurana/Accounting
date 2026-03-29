import React, { useState } from 'react';
import { View, Text, Button, StyleSheet, Alert, Platform } from 'react-native';
import * as DocumentPicker from 'expo-document-picker';

const API_URL = process.env.EXPO_PUBLIC_API_URL || 'http://localhost:8080/api';

export default function MigrationScreen() {
  const [file, setFile] = useState(null);

  const pickDocument = async () => {
    try {
      const result = await DocumentPicker.getDocumentAsync({
        type: ['text/csv', 'application/vnd.ms-excel'],
        copyToCacheDirectory: true,
      });
      if (result.type === 'success' || (result.assets && result.assets.length > 0)) {
        const asset = result.assets ? result.assets[0] : result;
        setFile(asset);
      }
    } catch (err) {
      console.log('Error picking document', err);
    }
  };

  const handleUpload = async () => {
    if (!file) {
      Alert.alert('Error', 'Please select a file first.');
      return;
    }

    const formData = new FormData();
    if (Platform.OS === 'web') {
      // Need to fetch blob from uri for web
      const res = await fetch(file.uri);
      const blob = await res.blob();
      formData.append('file', blob, file.name);
    } else {
      formData.append('file', {
        uri: file.uri,
        name: file.name,
        type: file.mimeType || 'text/csv'
      });
    }

    try {
      const response = await fetch(`${API_URL}/migration/import-parties`, {
        method: 'POST',
        body: formData,
        // Don't set Content-Type header manually for FormData, fetch handles the boundary
      });

      const data = await response.json();
      if (response.ok) {
        Alert.alert('Success', data.message);
        setFile(null);
      } else {
        Alert.alert('Error', data.error || 'Upload failed');
      }
    } catch (error) {
      console.error(error);
      Alert.alert('Error', 'Failed to upload file');
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.header}>Data Migration</Text>

      <View style={styles.card}>
        <Text style={styles.cardTitle}>Import Parties (Customers/Suppliers)</Text>
        <Text style={styles.instructions}>
          Upload a CSV file with columns: Name, Type (CUSTOMER/SUPPLIER/BOTH), GSTIN, Address, Phone.
        </Text>

        <View style={styles.buttonRow}>
          <Button title="Select CSV File" onPress={pickDocument} />
        </View>

        {file && (
          <Text style={styles.fileName}>Selected: {file.name}</Text>
        )}

        <View style={styles.buttonRow}>
          <Button title="Upload & Import" onPress={handleUpload} color="green" disabled={!file} />
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 20, backgroundColor: '#f5f5f5' },
  header: { fontSize: 24, fontWeight: 'bold', marginBottom: 20 },
  card: { backgroundColor: '#fff', padding: 20, borderRadius: 8, marginBottom: 20 },
  cardTitle: { fontSize: 18, fontWeight: 'bold', marginBottom: 10 },
  instructions: { marginBottom: 15, color: '#555' },
  buttonRow: { marginBottom: 10 },
  fileName: { marginVertical: 10, fontStyle: 'italic', color: 'blue' }
});

// Copyright (c) 2012, the Dart project authors.  Please see the AUTHORS file
// for details. All rights reserved. Use of this source code is governed by a
// BSD-style license that can be found in the LICENSE file.

#ifndef VM_DART_API_MESSAGE_H_
#define VM_DART_API_MESSAGE_H_

#include "vm/dart_api_state.h"
#include "vm/snapshot.h"

namespace dart {

// Use this C structure for reading internal objects in the serialized
// data. These are objects that we need to process in order to
// generate the Dart_CObject graph but that we don't want to expose in
// that graph.
// TODO(sjesse): Remove this when message serialization format is
// updated.
struct Dart_CObject_Internal : public Dart_CObject {
  enum Type {
    kTypeArguments = Dart_CObject::kNumberOfTypes,
    kDynamicType,
  };
};


// Reads a message snapshot into a C structure.
class ApiMessageReader : public BaseReader {
 public:
  ApiMessageReader(const uint8_t* buffer, intptr_t length, ReAlloc alloc);
  ~ApiMessageReader() { }

  Dart_CObject* ReadMessage();

 private:
  // Allocates a Dart_CObject object.
  Dart_CObject* AllocateDartCObject();
  // Allocates a Dart_CObject object with the specified type.
  Dart_CObject* AllocateDartCObject(Dart_CObject::Type type);
  // Allocates a Dart_CObject object for the null object.
  Dart_CObject* AllocateDartCObjectNull();
  // Allocates a Dart_CObject object for a boolean object.
  Dart_CObject* AllocateDartCObjectBool(bool value);
  // Allocates a Dart_CObject object for for a 32-bit integer.
  Dart_CObject* AllocateDartCObjectInt32(int32_t value);
  // Allocates a Dart_CObject object for for a 64-bit integer.
  Dart_CObject* AllocateDartCObjectInt64(int64_t value);
  // Allocates a Dart_CObject object for bigint data.
  Dart_CObject* AllocateDartCObjectBigint(intptr_t length);
  // Allocates a Dart_CObject object for a double.
  Dart_CObject* AllocateDartCObjectDouble(double value);
  // Allocates a Dart_CObject object for string data.
  Dart_CObject* AllocateDartCObjectString(intptr_t length);
  // Allocates a C Dart_CObject object for byte data.
  Dart_CObject* AllocateDartCObjectUint8Array(intptr_t length);
  // Allocates a C array of Dart_CObject objects.
  Dart_CObject* AllocateDartCObjectArray(intptr_t length);

  void Init();

  intptr_t LookupInternalClass(intptr_t class_header);
  Dart_CObject* ReadInlinedObject(intptr_t object_id);
  Dart_CObject* ReadObjectImpl(intptr_t header);
  Dart_CObject* ReadIndexedObject(intptr_t object_id);
  Dart_CObject* ReadObject();

  // Add object to backward references.
  void AddBackwardReference(intptr_t id, Dart_CObject* obj);

  Dart_CObject_Internal* AsInternal(Dart_CObject* object) {
    ASSERT(object->type >= Dart_CObject::kNumberOfTypes);
    return reinterpret_cast<Dart_CObject_Internal*>(object);
  }

  // Allocation of the structures for the decoded message happens
  // either in the supplied zone or using the supplied allocation
  // function.
  ReAlloc alloc_;
  ApiGrowableArray<Dart_CObject*> backward_references_;

  Dart_CObject type_arguments_marker;
  Dart_CObject dynamic_type_marker;
};


class ApiMessageWriter : public BaseWriter {
 public:
  ApiMessageWriter(uint8_t** buffer, ReAlloc alloc)
      : BaseWriter(buffer, alloc), object_id_(0) {
    ASSERT(kDartCObjectTypeMask >= Dart_CObject::kNumberOfTypes - 1);
  }
  ~ApiMessageWriter() { }

  // Writes a message of integers.
  void WriteMessage(intptr_t field_count, intptr_t *data);

  void WriteCMessage(Dart_CObject* object);

  void FinalizeBuffer() {
    BaseWriter::FinalizeBuffer(Snapshot::kMessage);
  }

 private:
  static const intptr_t kDartCObjectTypeBits = 4;
  static const intptr_t kDartCObjectTypeMask = (1 << kDartCObjectTypeBits) - 1;
  static const intptr_t kDartCObjectMarkMask = ~kDartCObjectTypeMask;
  static const intptr_t kDartCObjectMarkOffset = 1;

  void MarkCObject(Dart_CObject* object, intptr_t object_id);
  void UnmarkCObject(Dart_CObject* object);
  bool IsCObjectMarked(Dart_CObject* object);
  intptr_t GetMarkedCObjectMark(Dart_CObject* object);
  void UnmarkAllCObjects(Dart_CObject* object);

  void WriteSmi(int64_t value);
  void WriteMint(Dart_CObject* object, int64_t value);
  void WriteInt32(Dart_CObject* object);
  void WriteInt64(Dart_CObject* object);
  void WriteInlinedHeader(Dart_CObject* object);
  void WriteCObject(Dart_CObject* object);

  intptr_t object_id_;

  DISALLOW_COPY_AND_ASSIGN(ApiMessageWriter);
};

}  // namespace dart

#endif  // VM_DART_API_MESSAGE_H_

// Copyright (c) 2011, the Dart project authors.  Please see the AUTHORS file
// for details. All rights reserved. Use of this source code is governed by a
// BSD-style license that can be found in the LICENSE file.

#ifndef VM_OS_H_
#define VM_OS_H_

#include "vm/globals.h"

// Forward declarations.
class tm;

namespace dart {

// Forward declarations.
class Isolate;

// Interface to the underlying OS platform.
class OS {
 public:
  // Returns the abbreviated time-zone name for the given instant.
  // For example "CET" or "CEST".
  static const char* GetTimeZoneName(int64_t seconds_since_epoch);

  // Returns the difference in seconds between local time and UTC for the given
  // instant.
  // For example 3600 for CET, and 7200 for CEST.
  static int GetTimeZoneOffsetInSeconds(int64_t seconds_since_epoch);

  // Returns the difference in seconds between local time and UTC when no
  // daylight saving is active.
  // For example 3600 in CET and CEST.
  static int GetLocalTimeZoneAdjustmentInSeconds();

  // Returns the current time in milliseconds measured
  // from midnight January 1, 1970 UTC.
  static int64_t GetCurrentTimeMillis();

  // Returns the current time in microseconds measured
  // from midnight January 1, 1970 UTC.
  static int64_t GetCurrentTimeMicros();

  // Returns the activation frame alignment constraint or zero if
  // the platform doesn't care. Guaranteed to be a power of two.
  static word ActivationFrameAlignment();

  // This constant is guaranteed to be greater or equal to the
  // preferred code alignment on all platforms.
  static const int kMaxPreferredCodeAlignment = 32;

  // Returns the preferred code alignment or zero if
  // the platform doesn't care. Guaranteed to be a power of two.
  static word PreferredCodeAlignment();

  // Returns the stack size limit.
  static uword GetStackSizeLimit();

  // Returns number of available processor cores.
  static int NumberOfAvailableProcessors();

  // Sleep the currently executing thread for millis ms.
  static void Sleep(int64_t millis);

  // Print formatted output to stdout/stderr for debugging.
  static void Print(const char* format, ...);
  static void PrintErr(const char* format, ...);
  static void VFPrint(FILE* stream, const char* format, va_list args);
  // Print formatted output info a buffer.
  //
  // Does not write more than size characters (including the trailing '\0').
  //
  // Returns the number of characters (excluding the trailing '\0')
  // that would been written if the buffer had been big enough.  If
  // the return value is greater or equal than the given size then the
  // output has been truncated.  The return value is never negative.
  //
  // The buffer will always be terminated by a '\0', unless the buffer
  // is of size 0.  The buffer might be NULL if the size is 0.
  //
  // This specification conforms to C99 standard which is implemented
  // by glibc 2.1+ with one exception: the C99 standard allows a
  // negative return value.  We will terminate the vm rather than let
  // that occur.
  static int SNPrint(char* str, size_t size, const char* format, ...);
  static int VSNPrint(char* str, size_t size,
                      const char* format,
                      va_list args);

  // Converts a C string which represents a valid dart integer into a 64 bit
  // value.
  // Returns false if it is unable to convert the string to a 64 bit value,
  // the failure could be because of underflow/overflow or invalid characters.
  // On success the function returns true and 'value' contains the converted
  // value.
  static bool StringToInt64(const char* str, int64_t* value);

  // Initialize the OS class.
  static void InitOnce();

  // Shut down the OS class.
  static void Shutdown();

  static void Abort();

  static void Exit(int code);
};

}  // namespace dart

#endif  // VM_OS_H_

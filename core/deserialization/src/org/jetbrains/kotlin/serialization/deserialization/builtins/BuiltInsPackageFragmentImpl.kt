/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.serialization.deserialization.builtins

import org.jetbrains.kotlin.builtins.BuiltInsPackageFragment
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.metadata.ProtoBuf
import org.jetbrains.kotlin.metadata.builtins.BuiltInsBinaryVersion
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.serialization.deserialization.DeserializedPackageFragmentImpl
import org.jetbrains.kotlin.storage.StorageManager
import java.io.InputStream

class BuiltInsPackageFragmentImpl(
    fqName: FqName,
    storageManager: StorageManager,
    module: ModuleDescriptor,
    inputStream: InputStream
) : BuiltInsPackageFragment, DeserializedPackageFragmentImpl(fqName, storageManager, module, inputStream.use { stream ->
    val version = BuiltInsBinaryVersion.readFrom(stream)

    if (!version.isCompatible()) {
        // TODO: report a proper diagnostic
        throw UnsupportedOperationException(
            "Kotlin built-in definition format version is not supported: " +
                    "expected ${BuiltInsBinaryVersion.INSTANCE}, actual $version. " +
                    "Please update Kotlin"
        )
    }

    ProtoBuf.PackageFragment.parseFrom(stream, BuiltInSerializerProtocol.extensionRegistry)
}, containerSource = null)
